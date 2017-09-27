package com.connxun.app.controller;


import com.connxun.app.entity.Code;
import com.connxun.app.entity.JsonEntity;
import com.connxun.app.entity.JzUser;
import com.connxun.app.searchVO.JzUserSearchVO;
import com.connxun.app.service.JzUserService;
import com.connxun.util.code.SerialNumUtil;
import com.connxun.util.config.PubConfig;
import com.connxun.util.date.DateUtil;
import com.connxun.util.redis.RedisUtil;
import com.connxun.util.string.StringUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * 用户控制类
 * <p>
 * Created by Mac on 2017/6/11.
 */
@Controller
@RequestMapping("/jz/user")
public class JzUserController extends AppBaseController {

    //0正常 1删除  2 禁用 3 审核中
    @Autowired
    private JzUserService jzUserService;
    private final PubConfig pubConfig;
//    @Autowired
//    private LwInvitationService lwInvitationService;
//    @Autowired
//    private JzUserCourseService JzUserCourseService;
//    @Autowired
//    private LwCourseService lwCourseService;
//    @Autowired
//    private LwCourseChapterService lwCourseChapterService;
//    @Autowired
//    private LwTeacherService lwTeacherService;

    @Autowired
    public JzUserController(PubConfig pubConfig) {
        this.pubConfig = pubConfig;
    }

    //图片上传
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonEntity upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse
            response, ModelMap model) {
        JsonEntity json = new JsonEntity();
        String uploadPath = pubConfig.getImageUploadPath();
        String storePath = "/proPic/" + DateUtil.getShortSystemDate() + "/";
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        //        String fileName = new Date().getTime()+".jpg";
        String createFilename = DateUtil.getShortSystemTime() + SerialNumUtil.createRandowmNum(6) + "." + suffix;
        File targetFile = new File(uploadPath + storePath, createFilename);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        if (file.getSize() > 1024 * 1024) {
            json.setResultCode("1");
            json.setMessage("文件大于1M");

        } else {
            //保存
            try {
                file.transferTo(targetFile);
//                json = "{success:" + true + ",msgText:'" + "成功" + "',createFilename:'" + createFilename + "',createFilepath:'" + storePath + "'}";

                json.setResultCode("200");
                json.setData(storePath + createFilename);
                json.setMessage("上传成功");
            } catch (Exception e) {
                System.out.println(e.toString());
                json.setResultCode("1");
                json.setMessage("上传失败");
//                json = "{success:" + false + ",msgText:'" + "上传失败" + e.getMessage() + "'}";
                e.printStackTrace();
            }
        }
        return json;
    }

    @RequestMapping("/out")
    @ResponseBody
    public JsonEntity out(HttpServletRequest request, HttpServletResponse response, String userNo) {
//        RedisUtil.set(userNo, "000");

        RedisUtil.del(userNo);

        return booleanToJson(true);

    }

    //  获取用户自己的信息
    @ApiOperation(value="用户获取自己的信息")
    @RequestMapping(value="/info",method = RequestMethod.POST)
    @ResponseBody
    public JsonEntity info( @ApiParam(required = true, name = "userNo", value = "用户ID")String userNo) {

        JzUser user = jzUserService.findOne(Integer.parseInt(userNo));
        if (user != null) {
            user.setPassword(null);
        }
        return objectToJson(user);

    }

//    //   安卓下载
//    @RequestMapping(value = "/download")
//    @ResponseBody
//    public JsonEntity download(Integer courseChapterNo) {
//        LwCourseChapter lwCourseChapter = lwCourseChapterService.findOne(courseChapterNo);
//        if (lwCourseChapter != null) {
//            int courseNo = lwCourseChapter.getCourseId();
//            LwCourse lwCourse = lwCourseService.findOne(courseNo);
//            if (lwCourse != null) {
//                CourseDownload courseDownload = new CourseDownload();
//                courseDownload.setCourseName(lwCourse.getName());
//                courseDownload.setCourseNo(courseNo);
//                courseDownload.setEndDate(lwCourse.getEndDate());
//                courseDownload.setIcon(lwCourse.getIcon());
//                courseDownload.setStartDate(lwCourse.getStartDate());
//                courseDownload.setTitle(lwCourseChapter.getTitle());
//                courseDownload.setTimeLength(lwCourseChapter.getTimeLength());
//                courseDownload.setClassHour(lwCourse.getClassHour());
//                List<LwTeacher> lwTeachers = lwTeacherService.getCourseList(courseNo);
//                List<LwTeacherDTO> lwTeacherDTOS = new ArrayList<>();
//                if (lwTeachers.size() > 0) {
//                    for (LwTeacher lwTeacher : lwTeachers) {
//                        LwTeacherDTO lwTeacherDTO = new LwTeacherDTO();
//                        BeanUtils.copyProperties(lwTeacher, lwTeacherDTO);
//                        lwTeacherDTOS.add(lwTeacherDTO);
//                    }
//                }
//                courseDownload.setTeachers(lwTeacherDTOS);
//                json = objectToJson(courseDownload);
//            } else {
//                json = ErrorCode(Code.NOCOURSE);
//            }
//
//        } else {
//            json = ErrorCode(Code.NOCOURSECHATER);
//        }
//        return json;
//
//    }

    @ResponseBody
    @RequestMapping("/friendInfo")
    public JsonEntity friendInfo(int id) {
        JzUser user = jzUserService.findOne(id);
        if (user != null) {
            user.setPassword(null);
            user.setToken(null);
        }
        return objectToJson(user);

    }

    //用户搜索
    @RequestMapping(value = "search")
    @ResponseBody
    public JsonEntity search(JzUserSearchVO search) {
        List<JzUser> liveUsers = jzUserService.search(search);
        return objectToJson(liveUsers);

    }

    //    修改基础信息
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public JsonEntity updateDeclaration(JzUserSearchVO vo, Integer state) {
        JzUser sysUser = jzUserService.findOne(vo.getUserNo());

        if (sysUser != null) {

            if (state == 1) {

                sysUser.setDeclaration(vo.getDeclaration());
            }
            if (state == 2) {
                sysUser.setNickName(vo.getNickName());

            }
            if (state == 3) {
                sysUser.setSex(vo.getSex());
            }
            if (state == 4) {
                sysUser.setIcon(vo.getIcon());
            }


            sysUser = jzUserService.save(sysUser);
            if (sysUser != null) {
                sysUser.setPassword(null);
                json = objectToJson(sysUser);

            } else {
                json = booleanToJson(false);

            }
        } else {
            json = ErrorCode(Code.USERISNOT);

        }
        return json;
    }


    //    修改基础信息
    @ResponseBody
    @RequestMapping(value = "updateInfo", method = RequestMethod.POST)
    public JsonEntity updateInfo(JzUserSearchVO vo) {
        JzUser sysUser = jzUserService.findOne(vo.getUserNo());

        if (sysUser != null) {

            if (StringUtil.isNotNullOrEmpty(vo.getDeclaration())) {

                sysUser.setDeclaration(vo.getDeclaration());
            }
            if (StringUtil.isNotNullOrEmpty(vo.getNickName())) {
                sysUser.setNickName(vo.getNickName());

            }
            if (vo.getSex() != null) {
                sysUser.setSex(vo.getSex());
            }
            if (StringUtil.isNotNullOrEmpty(vo.getIcon())) {
                sysUser.setIcon(vo.getIcon());
            }

            sysUser = jzUserService.save(sysUser);
            if (sysUser != null) {
                sysUser.setPassword(null);
                json = objectToJson(sysUser);

            } else {
                json = booleanToJson(false);

            }
        } else {
            json = ErrorCode(Code.USERISNOT);

        }
        return json;
    }


    //修改手机号
    @ResponseBody
    @RequestMapping(value = "updatePhone", method = RequestMethod.POST)
    public JsonEntity updatePhone(JzUserSearchVO vo, String captch) {
        JzUser sysUser = jzUserService.findOne(vo.getUserNo());

        if (sysUser != null) {
            String sysCaptch = RedisUtil.get(vo.getPhone() + "1");
            System.out.println(sysCaptch);
            if (!StringUtil.isNotNullOrEmpty(sysCaptch)) {
                json = ErrorCode(Code.CAPTCHFAIL);
            } else {
                sysUser.setPhone(vo.getPhone());
                sysUser = jzUserService.save(sysUser);
                if (sysUser != null) {
                    sysUser.setPassword(null);
                    json = objectToJson(sysUser);

                } else {
                    json = booleanToJson(false);

                }
            }


        } else {
            json = ErrorCode(Code.USERISNOT);

        }
        return json;
    }
//    修改密码

    @ResponseBody
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    public JsonEntity updatePassword(String userNo, String password, String newPwd) {
        JzUser sysUser = jzUserService.findOne(Integer.parseInt(userNo));

        System.out.println(sysUser.getPassword());
        System.out.println(sysUser.getPassword().equals(password));
        if (sysUser != null && sysUser.getPassword().equals(password)) {
            sysUser.setPassword(newPwd);

            sysUser = jzUserService.save(sysUser);
            if (sysUser != null) {
                json = booleanToJson(true);

            } else {
                json = booleanToJson(false);

            }
        } else {
            json = ErrorCode(Code.LOGINFAIL);

        }
        return json;
    }

//// 用户的注册邀请
//
//    @ResponseBody
//    @RequestMapping(value = "/invitation", method = RequestMethod.POST)
//    public JsonEntity invitationGetList(JzUserSearchVO JzUserSearchVO) {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//
//        JzUser JzUser = JzUserService.findOne(JzUserSearchVO.getUserNo());
//        Integer inviteCode = JzUser.getInviteCode();
//        if (inviteCode == null || inviteCode == 0) {
//            JzUser.setInviteCode(JzUser.getId() + 10000);
//            JzUser = JzUserService.save(JzUser);
//            if (JzUser != null) {
//                map.put("inviteCode", JzUser.getInviteCode());
//                map.put("invitation", null);
//                map.put("userList", null);
//
//            }
//        } else {
//            map.put("inviteCode", JzUser.getInviteCode());
//            LwInvitationSearchVO lwInvitationSearchVO = new LwInvitationSearchVO();
//            lwInvitationSearchVO.setState(0);
////         获取最新的规则id
//            List<LwInvitation> lwInvitations = lwInvitationService.getList(lwInvitationSearchVO);
//            if (lwInvitations != null && lwInvitations.size() > 0) {
//                LwInvitation lwInvitation = lwInvitations.get(0);
//                Integer userNum = lwInvitation.getUserNum();
//                map.put("invitation", lwInvitations.get(0));
//                JzUserSearchVO.setGuide(JzUser.getId());
//                JzUserSearchVO.setInvitationNo(lwInvitations.get(0).getId());
////                查询邀请人数
//                JzUserSearchVO.setPage(0);
//                JzUserSearchVO.setLength(9999);
//                List<JzUser> list = JzUserService.getInvitationList(JzUserSearchVO);
//                int userSize = list.size();
//                if (userSize > 0) {
//                    map.put("userList", list);
//                    lwInvitation.setIsNum(userSize);
//                    lwInvitation.setNoNum(userNum > userSize ? userNum - userSize : 0);
////                    推送消息
//                    if (userSize >= userNum) {
//                        Integer userNo = JzUserSearchVO.getUserNo();
//                        Integer courseNo = lwInvitation.getCourseNo();
//                        LwCourse lwCourse = lwCourseService.findOne(courseNo);
////                        判断有无此课程   曾送课程
//                        Integer size = JzUserCourseService.isCourse(courseNo, userNo);
//                        if (size > 0) {
//                        } else {
////                           曾送课程
//                            JzUserCourse JzUserCourse = new JzUserCourse();
//                            JzUserCourse.setReview(0);
//                            JzUserCourse.setUserNo(userNo);
//                            JzUserCourse.setCourseNo(courseNo);
//                            JzUserCourse.setType(0);
//                            JzUserCourse.setFinalAmount(lwCourse.getFinalAmount());
//                            JzUserCourse.setCourseType(lwCourse.getCourseType());
//                            JzUserCourseService.save(JzUserCourse);
//                            // 发送推送消息
//                            try {
//                                TempletUtil.courseOpen(userNo + "", lwCourse.getName(), lwCourse.getId(), lwCourse.getViewEndDate());
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    }
//
//                } else {
//                    lwInvitation.setIsNum(0);
//                    lwInvitation.setNoNum(lwInvitation.getUserNum());
//                    map.put("userList", null);
//
//                }
//                map.put("invitation", lwInvitation);
//            } else {
//                map.put("invitation", null);
//                map.put("userList", null);
//            }
//        }
//        json = objectToJson(map);
//
//        return json;
//
//    }
//    用户扫码登录

    @ResponseBody
    @RequestMapping(value = "/phoneCode", method = RequestMethod.POST)
    public JsonEntity loginCode(JzUserSearchVO vo) {


        JzUser JzUser = jzUserService.findOne(vo.getUserNo());
        json = booleanToJson(false);
        if (JzUser != null) {
            String ret = RedisUtil.get(vo.getCode());
            if (StringUtil.isNotNullOrEmpty(ret)) {
                if (ret.equals(vo.getCodeUUID())) {
                    RedisUtil.set(vo.getCode(), vo.getUserNo());
                    String token = RedisUtil.get(vo.getUserNo() + "");
                    RedisUtil.set(vo.getCode() + "_token", token);
                    json = booleanToJson(true);


                }

            }


        }
        return json;

    }

    @ResponseBody
    @RequestMapping(value = "isLogin")
    public JsonEntity isLogin() {
        return booleanToJson(true);


    }


}
