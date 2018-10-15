/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elvis.example.chat;


public class Constant {
	private static String URL = "http://mjhzds.vicp.io/chat/";

	public static String URL_REGISTER = URL + "register";
	public static String URL_LOGIN = URL + "login";
	public static String URL_SESSIONREQUEST = URL + "session";

	public static String CHAT_REQUEST = "chat request";

	//聊天类型
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;

	//服务器代码
	public static int ERRORCODE_NULL = 200;
	public static int ERRORCODE_PWD = 201;
	public static int ERRORCODE_ACCOUNTNOTEXIST = 202;
	public static int ERRORCODE_ACCOUNTEXIST = 203;

	public static int SUCCESSCODE_LOGIN = 100;
	public static int SUCCESSCODE_REGISTER = 101;
	public static int SUCCESSCODE_UPLOADIMG = 102;
	public static int SUCCESSCODE_DOWNLOADIMG = 103;

	// 代码对应信息
	public static String ERRORMSG_NULL = "不能为空";
	public static String ERRORMSG_PWD = "密码错误";
	public static String ERRORMSG_ACCOUNTNOTEXIST = "账号不存在，请注册";
	public static String ERRORMSG_ACCOUNTEXIST = "账号已存在，请登录";
	public static String SUCCESSMSG_LOGIN = "登录成功";
	public static String SUCCESSMSG_REGISTER = "注册成功";

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

}
