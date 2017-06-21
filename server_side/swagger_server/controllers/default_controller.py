import pymysql
import connexion
from swagger_server.models.settings import Settings
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def settings_get(user_id):
    """
    restore user&#39;s settings
    
    :param user_id: 
    :type user_id: str

    :rtype: None
    """

    sql = """select setting_json from users_settings where id=(select max(id) from users_settings);"""

    json_setting = sql_query(sql, True)
    if json_setting:
        print("sending: " + json_setting)
        return json_setting

    return None


def settings_post(settings=None):
    """
    Store user&#39;s settings
    
    :param settings: The settings to insert.
    :type settings: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        settings = Settings.from_dict(connexion.request.get_json())
        user_id = settings.user_id
        timestemp = settings.timestemp
        setting_json = settings.setting_json

        # Prepare SQL query to INSERT a record into the database.
        sql = """INSERT INTO users_settings(user_id, timestemp, setting_json)
                       VALUES ('{0}', '{1}', '{2}')""".format(user_id, timestemp, setting_json)


        if sql_query(sql):
            return 'Insert successfully'

        return None






def sql_query(sql, res_flag = False):
    success_flag = True
    json_setting = None

    # Open database connection
    db = pymysql.connect("localhost", "root", "64324Sho_", "orcam_myeye")

    # prepare a cursor object using cursor() method
    cursor = db.cursor()

    try:
        # Execute the SQL command
        cursor.execute(sql)
        # Commit your changes in the database
        db.commit()

        for row in cursor:
            json_setting = row[0]

        if json_setting and json_setting.startswith('('):
            json_setting = None

    except Exception as e:
        success_flag = False
        json_setting = None
        # Rollback in case there is any error
        print(e)
        db.rollback()

    # disconnect from server
    db.close()
    if res_flag:
        return json_setting
    else:
        return success_flag