import json
import logging
import time

import scrapy
import pymysql


class MoocSpider(scrapy.Spider):
    name = 'course'
    allowed_domains = ['icourse163.org']
    start_urls = {
        'schoolId': 'https://www.icourse163.org/collegeAdmin/teacherPanel.htm',
        'stu_list': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.getAllStuInfoByClassroomId'
                    '.rpc?csrfKey=',
        'unit_list': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.getVideoViewLearnCount'
                     '.rpc?csrfKey=',
        'stu_learn': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.getUnitLearnMember.rpc'
                     '?csrfKey='
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

    def __init__(self, u=None, c=None, **kwargs):
        super().__init__(**kwargs)
        self.__db = pymysql.connect(host=self.__sql_host, user=self.__sql_user,
                                    password=self.__sql_pwd, db=self.__sql_db, port=self.__sql_port, charset='utf8')
        if u is None or c is None:
            raise ValueError('参数不能为空')
        self.u = u
        self.c = c
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
        print(self.__schoolId)
        data = {
            'classroomId': self.c
        }
        yield scrapy.FormRequest(
            self.start_urls['stu_list'] + self.csrfKey,
            callback=self.get_stuList,
            cookies=self.cookie,
            dont_filter=True,
            headers=self.__headers_post,
            method='POST',
            formdata=data
        )

    def get_stuList(self, response):
        json_data = json.loads(response.text)
        if len(json_data['result']) is 0:
            print(response.text)
            exit(2)
        for user in json_data['result']:
            self.save_user(user)
        data = {
            'classroomId': self.c
        }
        yield scrapy.FormRequest(
            self.start_urls['unit_list'] + self.csrfKey,
            callback=self.get_unitId,
            cookies=self.cookie,
            dont_filter=True,
            headers=self.__headers_post,
            method='POST',
            formdata=data
        )

    def get_unitId(self, response):
        json_data = json.loads(response.text)
        if len(json_data['result']) is 0:
            print(response.text)
            exit(3)
        for unit in json_data['result']:
            self.save_unit(unit)
            data = {
                'classroomId': self.c,
                'unitId': str(unit['unitId'])
            }
            yield scrapy.FormRequest(
                self.start_urls['stu_learn'] + self.csrfKey,
                callback=self.get_stuLearnInfo,
                cookies=self.cookie,
                dont_filter=True,
                headers=self.__headers_post,
                method='POST',
                formdata=data,
                meta={'unitId': unit['unitId']}
            )

    def get_stuLearnInfo(self, response):
        json_data = json.loads(response.text)
        if len(json_data['result']) is not 0:
            for info in json_data['result']:
                self.save_info(info,response.meta['unitId'])

    def save_user(self, user=None):
        if user is None:
            user = {}
        for i in user.keys():
            if user[i] is None:
                user[i] = 'NULL'

        sql_user = f'SELECT count(*) FROM mooc_student WHERE member_id=\'{user["memberId"]}\' AND classroom_id=\'{self.c}\''
        cur = self.__db.cursor()
        cur.execute(query=sql_user)
        result = cur.fetchone()
        cur.close()

        if int(result[0]) is not 0:
            sql_del = f'DELETE FROM mooc_student WHERE member_id=\'{user["memberId"]}\' AND classroom_id=\'{self.c}\''
            try:
                self.__db.query(sql_del)
                self.__db.commit()
            except Exception as e:
                self.__db.rollback()
                self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql_del}', level=logging.WARN)
                return

        str_ = "NULL" if user["number"] == "NULL" else '\''+user["number"]+'\''
        sql = f'INSERT INTO mooc_student(classroom_id,member_id,nickname,number,real_name) VALUES(\'{self.c}\',\'{user["memberId"]}\',\'{user["nickName"]}\',{str_},\'{user["realName"]}\')'
        try:
            self.__db.query(sql)
            self.__db.commit()
        except Exception as e:
            self.__db.rollback()
            self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql}', level=logging.WARN)

    def save_unit(self, unit=None):
        if unit is None:
            unit = {}

        for i in unit.keys():
            if unit[i] is None:
                unit[i] = 'NULL'

        sql_unit = f'SELECT count(*) FROM unit WHERE unit_id={unit["unitId"]}'
        cur = self.__db.cursor()
        cur.execute(query=sql_unit)
        result = cur.fetchone()
        cur.close()

        if int(result[0]) is not 0:
            sql_del = f'DELETE FROM unit WHERE unit_id={unit["unitId"]}'
            try:
                self.__db.query(sql_del)
                self.__db.commit()
            except Exception as e:
                self.__db.rollback()
                self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql_del}', level=logging.WARN)
                return

        sql = f'INSERT INTO unit' \
              f'(avg_duration,content_id,content_type,duration,unit_id,unit_name,view_member_count,view_times_avg_count) ' \
              f'VALUES(' \
              f'{unit["avgDuration"]},\'{unit["contentId"]}\',{unit["contentType"]},{unit["duration"]},{unit["unitId"]},\'{unit["unitName"]}\',{unit["viewMemberCount"]},{unit["viewTimesAvgCount"]})'

        try:
            self.__db.query(sql)
            self.__db.commit()
        except Exception as e:
            self.__db.rollback()
            self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql}', level=logging.WARN)

    def save_info(self, info=None,unitId=0):
        if info is None:
            info = {}
        for i in info.keys():
            if info[i] is None:
                info[i] = 'NULL'

        sql_stu = f'SELECT count(*) FROM units_learn_info WHERE unit_id={unitId} AND member_id={info["memberId"]}'
        cur = self.__db.cursor()
        cur.execute(query=sql_stu)
        result = cur.fetchone()
        cur.close()

        if int(result[0]) is not 0:
            sql_del = f'DELETE FROM units_learn_info WHERE unit_id={unitId} AND member_id={info["memberId"]}'
            try:
                self.__db.query(sql_del)
                self.__db.commit()
            except Exception as e:
                self.__db.rollback()
                self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql_del}', level=logging.WARN)
                return

        sql = f'INSERT INTO units_learn_info(classroom_id,learned_video_time_count,member_id,score,start_time,unit_id) VALUES(' \
              f'{self.c},{info["learnedVideoTimeCount"]},{info["memberId"]},{info["score"]},\'{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(int(info["startTime"]) / 1000))}\',{unitId})'
        try:
            self.__db.query(sql)
            self.__db.commit()
        except Exception as e:
            self.__db.rollback()
            self.log(f'事务执行失败，原因:{str(e)},sql语句为:{sql}', level=logging.WARN)

