import json

import pymysql
import scrapy


class MoocSpider(scrapy.Spider):
    name = "grade"
    allowed_domains = ['https://www.icourse163.org/']
    start_url = {
        'stu_learn': 'https://www.icourse163.org/mm-classroom/web/j/mocTermClassroomRpcBean.classStuStatistics.rpc?csrfKey='
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
        for i in self.cookie:
            i = i.__str__()
            if i.find('NTESSTUDYSI') != -1:
                self.csrfKey = i.split('\'value\': \'')[1].split('\'')[0]
        # 将Cookie放入请求头中
        self.__headers_post.update({'edu-script-token': self.csrfKey})

    def get_cookie(self):
        sql = f'select mooc_cookie from mooc_user where id={self.u}'
        cur = self.__db.cursor()
        cur.execute(sql)
        results = cur.fetchall()
        if results[0][0] is None:
            raise ValueError('Cookie不能为空')
        return json.loads(results[0][0])

    def start_requests(self):
        data = {
            'classroomId': self.c,
            'pageSize': '1000',
            'pageIndex': '1'
        }
        yield scrapy.FormRequest(
            url=self.start_url['stu_learn']+self.csrfKey,
            callback=self.get_Learn,
            cookies=self.cookie,
            dont_filter=True,
            headers=self.__headers_post,
            formdata=data
        )

    def get_Learn(self, response):
        json_data = json.loads(response.text)
        for stu in json_data['result']['studentInfo']:
            pass
