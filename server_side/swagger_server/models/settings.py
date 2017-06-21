# coding: utf-8

from __future__ import absolute_import
from .base_model_ import Model
from datetime import date, datetime
from typing import List, Dict
from ..util import deserialize_model


class Settings(Model):
    """
    NOTE: This class is auto generated by the swagger code generator program.
    Do not edit the class manually.
    """
    def __init__(self, setting_json: str=None, timestemp: str=None, user_id: str=None):
        """
        Settings - a model defined in Swagger

        :param setting_json: The setting_json of this Settings.
        :type setting_json: str
        :param timestemp: The timestemp of this Settings.
        :type timestemp: str
        :param user_id: The user_id of this Settings.
        :type user_id: str
        """
        self.swagger_types = {
            'setting_json': str,
            'timestemp': str,
            'user_id': str
        }

        self.attribute_map = {
            'setting_json': 'setting_json',
            'timestemp': 'timestemp',
            'user_id': 'user_id'
        }

        self._setting_json = setting_json
        self._timestemp = timestemp
        self._user_id = user_id

    @classmethod
    def from_dict(cls, dikt) -> 'Settings':
        """
        Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The settings of this Settings.
        :rtype: Settings
        """
        return deserialize_model(dikt, cls)

    @property
    def setting_json(self) -> str:
        """
        Gets the setting_json of this Settings.

        :return: The setting_json of this Settings.
        :rtype: str
        """
        return self._setting_json

    @setting_json.setter
    def setting_json(self, setting_json: str):
        """
        Sets the setting_json of this Settings.

        :param setting_json: The setting_json of this Settings.
        :type setting_json: str
        """

        self._setting_json = setting_json

    @property
    def timestemp(self) -> str:
        """
        Gets the timestemp of this Settings.

        :return: The timestemp of this Settings.
        :rtype: str
        """
        return self._timestemp

    @timestemp.setter
    def timestemp(self, timestemp: str):
        """
        Sets the timestemp of this Settings.

        :param timestemp: The timestemp of this Settings.
        :type timestemp: str
        """

        self._timestemp = timestemp

    @property
    def user_id(self) -> str:
        """
        Gets the user_id of this Settings.

        :return: The user_id of this Settings.
        :rtype: str
        """
        return self._user_id

    @user_id.setter
    def user_id(self, user_id: str):
        """
        Sets the user_id of this Settings.

        :param user_id: The user_id of this Settings.
        :type user_id: str
        """
        if user_id is None:
            raise ValueError("Invalid value for `user_id`, must not be `None`")

        self._user_id = user_id
