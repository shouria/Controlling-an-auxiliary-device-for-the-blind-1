# coding: utf-8

from __future__ import absolute_import

from swagger_server.models.settings import Settings
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestDefaultController(BaseTestCase):
    """ DefaultController integration test stubs """

    def test_settings_get(self):
        """
        Test case for settings_get

        restore user's settings
        """
        query_string = [('user_id', 'user_id_example')]
        response = self.client.open('/restoreSettings',
                                    method='GET',
                                    content_type='application/json',
                                    query_string=query_string)
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_settings_post(self):
        """
        Test case for settings_post

        Store user's settings
        """
        settings = Settings()
        response = self.client.open('/uploadSettings',
                                    method='POST',
                                    data=json.dumps(settings),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
