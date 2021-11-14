import json
import logging
import time

import scrapy
import pymysql


class MoocSpider(scrapy.Spider):
    name = 'mooc'
    allowed_domains = ['https://www.icourse163.org/']
    start_urls = {
        'schoolId': 'https://www.icourse163.org/collegeAdmin/teacherPanel.htm',
        'classId': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.pageClassroomByTeacher.rpc'
                   '?csrfKey=',
        'allClass': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.pageClassroomByCourseId.rpc'
                    '?csrfKey=',
    }

    __headers_get = {
        ':authority': 'www.icourse163.org',
        ':method': 'GET',
        ':scheme': 'https',
        'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,'
                  'application/signed-exchange;v=b3;q=0.9',
        'accept-encoding': 'gzip, deflate, br',
        'accept-language': 'zh-CN,zh;q=0.9',
        'cache-control': 'max-age=0',
        'sec-ch-ua': '"Google Chrome";v="93", " Not;A Brand";v="99", "Chromium";v="93"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'document',
        'sec-fetch-mode': 'navigate',
        'sec-fetch-site': 'same-origin',
        'sec-fetch-user': '?1',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                      'Chrome/93.0.4577.82 Safari/537.36 ',
    }
    # Post方法请求头
    __headers_post = {
        ':authority': 'www.icourse163.org',
        ':method': 'POST',
        ':scheme': 'https',
        'accept': '*/*',
        'accept-encoding': 'gzip, deflate, br',
        'accept-language': 'zh-CN,zh;q=0.9',
        'content-type': 'application/x-www-form-urlencoded',
        'sec-ch-ua': '"Google Chrome";v="93", " Not;A Brand";v="99", "Chromium";v="93"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'same-origin',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                      'Chrome/93.0.4577.82 Safari/537.36 ',
        'origin': 'https://www.icourse163.org'
    }

    __sql_host = 'localhost'
    __sql_port = 3306
    __sql_user = 'root'
    __sql_pwd = '0000'
    __sql_db = 'MoocWeb'
    csrfKey = ''

    def __init__(self, u=None, **kwargs):
        super().__init__(**kwargs)
        self.__db = pymysql.connect(host=self.__sql_host, user=self.__sql_user,
                                    password=self.__sql_pwd, db=self.__sql_db, port=self.__sql_port, charset='utf8')
        if u is None:
            raise ValueError('参数不能为空')
        self.u = u
        self.cookie = self.get_cookie()
        self.__schoolId = ''

    def get_cookie(self):
        sql = f'select mooc_cookie from mooc_user where id={self.u}'
        cur = self.__db.cursor()
        cur.execute(sql)
        results = cur.fetchall()
        if results[0][0] is None:
            raise ValueError('Cookie不能为空')
        return json.loads(results[0][0])

    def start_requests(self):
        for i in self.cookie:
            i = i.__str__()
            if i.find('NTESSTUDYSI') != -1:
                self.csrfKey = i.split('\'value\': \'')[1].split('\'')[0]
        # 将Cookie放入请求头中
        self.__headers_post.update({'edu-script-token': self.csrfKey})
        yield scrapy.Request(
            self.start_urls['schoolId'],
            callback=self.get_schoolId,
            cookies=self.cookie,
            dont_filter=True,
            headers=self.__headers_get
        )

    def get_schoolId(self, response):
        try:
            self.__schoolId = str(response.text).split(r'schoolId:"')[1].split(r'",')[0]
        except IndexError:  # Cookie过期 重新获得Cookie
            exit(3)
        data = {
            'schoolId': self.__schoolId,
            'pageIndex': '1',
            'pageSize': '100'
        }
        yield scrapy.FormRequest(
            self.start_urls['classId'] + self.csrfKey,
            callback=self.get_classIds,
            cookies=self.cookie,
            dont_filter=True,
            headers=self.__headers_post,
            method='POST',
            formdata=data
        )

    def get_classIds(self, response):
        json_data = json.loads(response.text)
        courseId_list = []
        for i in json_data['result']['list']:
            courseId_list.append(i['onlineCourseId'])
            data = {
                'courseId': str(i['onlineCourseId']),
                'pageIndex': '1',
                'pageSize': '100'
            }
            yield scrapy.FormRequest(
                self.start_urls['allClass'] + self.csrfKey,
                callback=self.get_allClass,
                cookies=self.cookie,
                dont_filter=True,
                headers=self.__headers_post,
                meta={'courseId': str(i['onlineCourseId'])},
                method='POST',
                formdata=data
            )

    def get_allClass(self, response):
        json_data = json.loads(response.text)
        for classInfo in json_data['result']['list']:
            self.saveClass(int(response.meta['courseId']), classInfo)

    def saveClass(self, course_id=0, classInfo=None):
        if classInfo is None:
            classInfo = {}
        for i in classInfo.keys():
            if classInfo[i] is None:
                classInfo[i] = 'NULL'
        sql_exist = f'SELECT count(*) AS COUNT FROM mooc_course WHERE classroom_id={classInfo["classroomId"]}'

        cur = self.__db.cursor()
        cur.execute(query=sql_exist)
        result = cur.fetchone()
        cur.close()

        if int(result[0]) is not 0:
            sql_del = f'DELETE FROM mooc_course WHERE classroom_id={classInfo["classroomId"]}'
            try:
                self.__db.query(sql_del)
                self.__db.commit()
            except Exception as e:
                self.__db.rollback()
                self.log(f'事务执行失败，原因:{str(e)}', level=logging.WARN)
                return

        sql = f'INSERT INTO mooc_course(user_id,school_id,course_id,course_name,can_teach,classroom_id,classroom_time,' \
              f'college_id,course_mode,creator_name,enroll_count,entrance_code,lesson_count,link_online_term_end_time,' \
              f'link_online_term_id,link_online_term_start_time,term_id,type,web_visible) VALUES(' \
              f'{self.u},{self.__schoolId},{course_id},\'{classInfo["classroomName"]}\',{1 if classInfo["canTeach"] else 0},{classInfo["classroomId"]},\'{classInfo["classroomTime"]}\',' \
              f'{classInfo["collegeId"]},{classInfo["courseMode"]},{classInfo["creatorName"]},{classInfo["enrollCount"]},\'{classInfo["entranceCode"]}\',{classInfo["lessonCount"]},\'{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(classInfo["linkOnlineTermEndTime"] / 1000))}\',' \
              f'{classInfo["linkOnlineTermId"]},\'{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(int(classInfo["linkOnlineTermStartTime"]) / 1000))}\',{classInfo["termId"]},{classInfo["type"]},{classInfo["webVisible"]})'
        try:
            self.__db.query(sql)
            self.__db.commit()
        except Exception as e:
            self.__db.rollback()
            self.log(f'事务执行失败，原因:{str(e)}', level=logging.WARN)
