package com.connxun.app.controller;


import com.connxun.app.entity.BeeCloud;
import com.connxun.app.entity.Code;
import com.connxun.app.entity.JsonEntity;
import com.connxun.app.entity.JzUser;
import com.connxun.app.searchVO.JzUserSearchVO;
import com.connxun.app.service.JzUserService;
import com.connxun.util.aliyun.sendsms.AliyunSms;
import com.connxun.util.easemob.tool.JsonTool;
import com.connxun.util.redis.RedisUtil;
import com.connxun.util.string.StringUtil;
import com.tls.entity.TlsAccountEntity;
import com.tls.sigcheck.TlsApiUtil;
import com.tls.sigcheck.TlsSigUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mac on 2017/6/11.
 */
@Controller
@RequestMapping(value = "/jz")
public class JzLoginController extends AppBaseController {

    private static Logger logger = LoggerFactory.getLogger("controllerLog");
    //0正常 1删除  2 禁用 3 审核中
    private Integer orderDel;

    public Integer getOrderDel() {
        return orderDel;
    }

    public void setOrderDel(Integer orderDel) {
        this.orderDel = orderDel;
    }

    @Autowired
    private JzUserService JzUserService;
//    public LwLoginController(JzUserService JzUserService) {
//        this.JzUserService = JzUserService;
//    }

    @RequestMapping(value = "/nll")
    public void null_() {
        Object obj = null;
        obj.toString();
    }

    // 手机登录
//    todo 判断是否手机登录
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "前端用户登录")
    public JsonEntity login(HttpServletRequest request, HttpServletResponse response,
                            @ApiParam(required = true, name = "phone", value = "手机号") String phone,
                            @ApiParam(required = true, name = "password", value = "密码") String password,
                            @ApiParam(required = true, name = "isApp", value = "是否为app用户") Integer isApp) {
       /* if (getOrderDel() == null) {
            CronTriggerRunner.orderDel();
            setOrderDel(1);

        }*/
        if (StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(password)) {
            logger.debug(phone + "参数错误");
            return booleanToJson(false, "参数错误");
        } else {
            JzUser user = JzUserService.getPhone(phone);

//      手机号校验
            if (user != null) {
//            用户状态
                if (user.getState() != 2) {
//                用户密码
                    if (user.getPassword().equals(StringUtil.getMD5(password))) {
                        JzUserService.updateLogin(user, request, isApp);

                        String ip = StringUtil.getIp(request);
                        user.setPassword(null);
                        //设置用户token
                        if (!RedisUtil.contain(user.getId() + "")) {
                            user.setToken(StringUtil.getNonceStr());
                            RedisUtil.set(user.getId() + "", user.getToken());
                        } else {
                            user.setToken(RedisUtil.get(user.getId() + ""));
                        }
                        /*设置用户sig*/
                        user.setUserSig(TlsSigUtil.genSig(user.getId() + "").getSig());
                        request.getSession().setAttribute("userSession", user);

                        return objectToJson(user);
                    } else {
                        return ErrorCode(Code.LOGINFAIL);
                    }
                } else {
                    return ErrorCode(Code.AUDITFAIL);
                }
            } else {
                return ErrorCode(Code.PHONEFAIL);
            }
        }

    }

//    //    微信登录
//    @RequestMapping(value = "/openLogin", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonEntity openLogin(String openId) {
//        JzUserSearchVO searchVO = new JzUserSearchVO();
//        searchVO.setOpenId(openId);
//        searchVO.setLoginType(1);
//        JzUser user = thirdLogin(searchVO);
//        if (user != null) {
//            user.setPassword(null);
//            user.setToken(StringUtil.getNonceStr());
//            RedisUtil.set(user.getId() + "", user.getToken());
//            return objectToJson(user);
//
//        } else {
//            return booleanToJson(false);
//        }
//
//    }
//
//    //    微博登录
//    @RequestMapping(value = "/wbLogin", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonEntity wbLogin(String wbId) {
//        JzUserSearchVO searchVO = new JzUserSearchVO();
//        searchVO.setWbId(wbId);
//        searchVO.setLoginType(2);
//        JzUser user = thirdLogin(searchVO);
//        if (user != null) {
//            user.setPassword(null);
//            user.setToken(StringUtil.getNonceStr());
//            RedisUtil.set(user.getId() + "", user.getToken());
//            return objectToJson(user);
//
//        } else {
//            return booleanToJson(false);
//        }
//
//    }
//
//    //    qq登录
//    @RequestMapping(value = "/qqLogin", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonEntity qqLogin(String qqId) {
//        JzUserSearchVO searchVO = new JzUserSearchVO();
//
//        searchVO.setOpenId(qqId);
//        searchVO.setLoginType(3);
//        JzUser JzUser = thirdLogin(searchVO);
//        JzUser user = thirdLogin(searchVO);
//        if (user != null) {
//            user.setPassword(null);
//            user.setToken(StringUtil.getNonceStr());
//            RedisUtil.set(user.getId() + "", user.getToken());
//            return objectToJson(user);
//
//        } else {
//            return booleanToJson(false);
//        }
//
//    }
//
//    public JzUser thirdLogin(JzUserSearchVO searchVO) {
//        searchVO.setPage(0);
//        List<JzUser> list = JzUserService.getList(searchVO);
//        if (list.size() > 0) {
//            return list.get(0);
//        } else {
//            return null;
//        }
//
//
//    }

    /**
     * 获取验证码
     *
     * @param phone //     * @param state 1 注册验证码   2 修改密码验证码  3 直接登录
     * @return
     */
    @ApiOperation(value = "验证码获取接口", notes = "验证码获取接口")
    @RequestMapping(value = "/getCaptch", method = RequestMethod.GET)
    @ResponseBody
    public JsonEntity recaptch(@ApiParam(required = true, name = "phone", value = "手机号") String phone,
                               @ApiParam(required = true, name = "state", value = "1 注册验证码   2 修改密码验证码  3 直接登录") int state) {
        System.out.println("----------------------------------");
        JzUser user = JzUserService.getPhone(phone);
        json = ErrorCode(Code.FAIL);
        if (state == 1) {

            if (user != null) {
                json = ErrorCode(Code.PHONEREPEAT);
            } else {
//            TODO 发送验证码
                for (int i=0;i<10;i++){
                    String captcher = StringUtil.buildRandom(6) + "";
                    AliyunSms.sendSms(phone, captcher);
                    RedisUtil.set(phone + "1", captcher);
                    System.out.println("===============" + RedisUtil.get(phone + "1") + "");
                    json = objectToJson(captcher);
                }


            }

        }
        if (state == 2) {
            if (user == null) {
                json = ErrorCode(Code.PHONEFAIL);
            } else {
//             TODO 发送验证码
                String captcher = StringUtil.buildRandom(6) + "";
                AliyunSms.sendSms(phone, captcher);

                RedisUtil.set(phone + "2", captcher);
                System.out.println("===============" + RedisUtil.get(phone + "2") + "");
                json = objectToJson(captcher);

            }

        }

        if (state == 3) {
            if (user == null) {
                json = ErrorCode(Code.PHONEFAIL);
            } else {
//            TODO 发送验证码
                String captcher = StringUtil.buildRandom(6) + "";
                AliyunSms.sendSms(phone, captcher);

                RedisUtil.set(phone + "3", captcher);
                System.out.println("===============" + RedisUtil.get(phone + "3") + "");
                json = objectToJson(captcher);

            }

        }
        return json;

    }

    @RequestMapping(value = "/regMsg", method = RequestMethod.POST)
    @ResponseBody
    public JsonEntity regMsg(Integer userNo) {
        JzUser JzUser = JzUserService.findOne(userNo);
        if (JzUser != null) {
            String msg = TempletUtil.reg(userNo + "");
        }
        return booleanToJson(true);

    }

    //注册
    @ApiOperation(value = "用户注册接口")
    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    @ResponseBody
    public JsonEntity reg(HttpServletRequest request, HttpServletResponse response,
                          @ApiParam(required = true, name = "phone", value = "手机号") String phone,
                          @ApiParam(required = true, name = "password", value = "密码") String password,
                          @ApiParam(required = true, name = "captch", value = "验证码") String captch,
                          @ApiParam(required = true, name = "inviteCode", value = "邀请码") Integer inviteCode) {
        JsonEntity json = new JsonEntity();
//        LwInvitationSearchVO vo = new LwInvitationSearchVO();
//        vo.setState(0);
        // todo 邀请码鉴定
        int guide = 0;
        int invitationNo = 0;
//        if (inviteCode != null) {
//            inviteCode = inviteCode - 10000;
//
//            JzUser JzUser = JzUserService.findOne(inviteCode);
//            if (JzUser != null) {
//                guide = inviteCode;
//
//                List<LwInvitation> lwInvitations = lwInvitationService.getList(vo);
//                if (lwInvitations != null && lwInvitations.size() > 0) {
//                    invitationNo = lwInvitations.get(0).getId();
//                }
//            }
//        }
        if (StringUtil.isNotNullOrEmpty(password) && StringUtil.isNotNullOrEmpty(phone)) {


            String sysCaptch = RedisUtil.get(phone + "1");
            if (!StringUtil.isNotNullOrEmpty(sysCaptch)) {
                json = ErrorCode(Code.FAIL);
            } else {
                if (sysCaptch.equals(captch)) {
                    JzUser user = new JzUser();
                    user.setPhone(phone);
                    user.setPassword(StringUtil.getMD5(password));
                    user.setNickName(StringUtil.getNonceStr());
//                    领路人
                    user.setGuide(guide);
//                    注册规则ID
                    user.setIntegral(0);
                    user.setInvitationNo(invitationNo);
                    user.setIcon("/proPic/20170730/20170730102138563982.jpg");
//                    user.setInviteCode();
                    user = JzUserService.save(user);
                    if (user != null) {
//                        推送消息

                        user.setToken(StringUtil.getNonceStr());
                        /*清空redis中的验证码————保证每个验证码只能被使用一次*/
                        RedisUtil.del(phone + "1");
                        /*保存用户登录token*/
                        RedisUtil.set(user.getId() + "", user.getToken());
                       /*向云端导入用户*/
                        TlsAccountEntity tlsAccountEntity = new TlsAccountEntity();
                        tlsAccountEntity.setIdentifier(user.getId()+"");
                        TlsApiUtil.accountImport(user.getId()+"",
                                TlsSigUtil.genSig(user.getId()+"").getSig(),
                                tlsAccountEntity);
                        json = objectToJson(user);
                    }
                } else {
                    json = ErrorCode(Code.CAPTCHFAIL);
                }
            }
        } else {
            json = ErrorCode(Code.FAIL);
        }
        return json;
    }

    /**
     * 用户修改密码接口
     *
     * @param request
     * @param response
     * @param phone    手机号
     * @param password 新密码
     * @param captch   验证码
     * @return
     */
    @ApiOperation(value = "用户修改密码接口")
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @ResponseBody
    public JsonEntity updatePwd(HttpServletRequest request, HttpServletResponse response,
                                @ApiParam(required = true, name = "phone", value = "手机号") String phone,
                                @ApiParam(required = true, name = "password", value = "新密码") String password,
                                @ApiParam(required = true, name = "captch", value = "验证码") String captch) {
        JsonEntity json = new JsonEntity();
        if (StringUtil.isNotNullOrEmpty(password) && StringUtil.isNotNullOrEmpty(phone)) {
            String sysCaptch = RedisUtil.get(phone + "2");
            JzUser user = JzUserService.getPhone(phone);
            if (sysCaptch.equals(captch) && user != null) {
                user.setPassword(password);
                user = JzUserService.save(user);
                if (user != null) {
                    user.setPassword(null);
                    user.setToken(StringUtil.getNonceStr());
                    RedisUtil.set(phone + "2", StringUtil.getNonceStr());
                    RedisUtil.set(user.getId() + "", user.getToken());

                    json = objectToJson(user);
                } else {
                    json = ErrorCode(Code.FAIL);
                }
            } else {
                json = ErrorCode(Code.CAPTCHFAIL);
            }
        } else {
            json = ErrorCode(Code.FAIL);
        }
        return json;

    }

    /**
     * 手机验证码登录接口
     *
     * @param phone  手机号
     * @param captch 验证码
     * @return
     */
    @ApiOperation(value = "手机验证码登录接口")
    @RequestMapping(value = "/loginCaptch", method = RequestMethod.GET)
    @ResponseBody
    public JsonEntity loginCaptch(
            @ApiParam(required = true, name = "phone", value = "手机号") String phone,
            @ApiParam(required = true, name = "captch", value = "验证码") String captch) {

        JzUser JzUser = JzUserService.getPhone(phone);
        if (JzUser != null) {
            String sysCaptch = "123456";
//            String sysCaptch = RedisUtil.get(phone + "3", String.class);
            if (sysCaptch.equals(captch)) {
                json = objectToJson(JzUser);

            } else {
                json = ErrorCode(Code.CAPTCHFAIL);
            }
        } else {
            json = ErrorCode(Code.PHONEFAIL);
        }
        return json;
    }

    /**
     * token作废接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/tokenFail")
    public JsonEntity tokenFail() {

        return ErrorCode(Code.TOKENFAIL);
    }

    //    pc端请求 返回时间 与UUID
    @RequestMapping(value = "/requestCode", method = RequestMethod.GET)
    @ResponseBody
    public JsonEntity requestCode() {
        Map<String, String> map = new HashMap<String, String>();
        long time = System.currentTimeMillis();
        String uuid = StringUtil.getMD5_2(String.valueOf(time + StringUtil.getNonceStr()));
        map.put("code", String.valueOf(time));
        map.put("codeUUID", uuid);
        RedisUtil.set(String.valueOf(time), uuid);

        return objectToJson(map);
    }
//    pc 端轮训

    @RequestMapping(value = "/loginCode", method = RequestMethod.GET)
    @ResponseBody
    public JsonEntity loginCode(JzUserSearchVO vo) {
        String userNo = RedisUtil.get(vo.getCode()) + "";


        json = objectToJson(false);
        if (StringUtil.isInteger(userNo)) {
            JzUser JzUser = JzUserService.findOne(Integer.parseInt(userNo));
            if (JzUser != null) {
                RedisUtil.set(vo.getCode(), "");
                JzUser.setPassword(null);
                JzUser.setQqId(null);
                JzUser.setWbId(null);
                JzUser.setOpenId(null);
                String token = RedisUtil.get(userNo + "");
                JzUser.setToken(token);
                RedisUtil.set(JzUser.getId() + "", JzUser.getToken(), 86400);
                json = objectToJson(JzUser);


            }
        }
        return json;

    }


    @RequestMapping("/getRedisValue")
    @ResponseBody
    public JsonEntity getRedisValue(HttpServletRequest request, HttpServletResponse response, String userNo) {
        String token = RedisUtil.get(userNo);

        return objectToJson(token);

    }

    //    邀请码 是否存在
    @RequestMapping(value = "isInvite", method = RequestMethod.GET)
    @ResponseBody
    public JsonEntity isInvite(Integer inviteCode) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        JzUser JzUser = JzUserService.isInvite(inviteCode);
        if (JzUser != null) {
            map.put("state", 1);
            json = objectToJson(map);

        } else {

            map.put("state", 0);
            json = objectToJson(map);
        }

        return json;
    }


//    @ResponseBody
//    @RequestMapping(value = "/webhook")
//    public void webhook(HttpServletRequest request, HttpServletResponse response) {
//        StringBuffer json = new StringBuffer();
//        String line = null;
//        try {
//            request.setCharacterEncoding("utf-8");
//            BufferedReader reader = request.getReader();
//            while ((line = reader.readLine()) != null) {
//                json.append(line);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BeeCloud beeCloud = new BeeCloud();
//        System.out.println("---------" + json.toString());
//        try {
//            beeCloud = (BeeCloud) JsonTool.read(json.toString(), BeeCloud.class);
//            System.out.println("----=========*********------" + beeCloud.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info("beeCloud_________" + beeCloud.toString());
//        // TODO: 2017-09-19
//        if (StringUtil.isNotNullOrEmpty(beeCloud.getSignature())) {
//
//            String state = lwOrderService.addwebhook(beeCloud.getSignature(), beeCloud.getTransaction_id()
//                    , beeCloud.getTransaction_type(), beeCloud.getChannel_type(), beeCloud.getTransaction_fee());
//            response.setHeader("Content-type", "text/html;charset=UTF-8");//向浏览器发送一个响应头，设置浏览器的解码方式为UTF-8
//            OutputStream stream = null;
//            try {
//                stream = response.getOutputStream();
//                stream.write(state.getBytes("UTF-8"));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }


    @ResponseBody
    @RequestMapping(value = "/webhookTest")
    public void webhookTest(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer json = new StringBuffer();
        String line = null;
        try {
            request.setCharacterEncoding("utf-8");
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeeCloud beeCloud = new BeeCloud();
        System.out.println("---------" + json.toString());
        try {
            beeCloud = (BeeCloud) JsonTool.read(json.toString(), BeeCloud.class);
            System.out.println("----------" + beeCloud.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (true) {
            String state = "success";

            response.setHeader("Content-type", "text/html;charset=UTF-8");//向浏览器发送一个响应头，设置浏览器的解码方式为UTF-8

            OutputStream stream = null;
            try {
                stream = response.getOutputStream();
                stream.write(state.getBytes("UTF-8"));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
