package zzuli.zw.request;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import zzuli.zw.config.Router;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.pojo.FileInfo;
import zzuli.zw.pojo.PhotoWall;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.FilePath;
import zzuli.zw.service.interfaces.FileInfoService;
import zzuli.zw.service.interfaces.PhotoWallService;
import zzuli.zw.service.interfaces.UserService;
import zzuli.zw.utils.FileUtil;
import zzuli.zw.utils.UploadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName UserRequest
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 15日 17:01
 * @Version: 1.0
 */
@Request
public class UserRequest {
    @Injection(name = "userService")
    private UserService userService;
    @Injection(name = "fileInfoService")
    private FileInfoService fileInfoService;
    @Injection(name = "photoWallService")
    private PhotoWallService photoWallService;

    /**
    * @Author 索半斤
    * @Description 查找用户详细信息
    * @Date 15:00 2022/11/26
    * @Param [userId, type, request, response]
    * @return void
    **/
    @RequestMapping(request = Router.FIND_DETAIL_USER_INFO)
    public void findUserDetailInfoRequest(@ParameterName("user")int userId,
                                                     @ParameterName("type")int type,
                                                     RequestParameter request,
                                                     ResponseParameter response) throws IOException {
        User userInfo = userService.findDetailUserInfo(userId);
        PhotoWall userPhotoWall = photoWallService.findUserPhotoWall(userInfo.getId());
        userInfo.setPhotoWall(userPhotoWall);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setSessionId(request.getSession().getId());
        if (type == 0)responseMessage.setRequest(Router.FIND_DETAIL_USER_INFO);
        else if (type == 1)responseMessage.setRequest(Router.FIND_DETAIL_FRIEND_INFO);
        if (userInfo == null){
            responseMessage.setCode(ResponseCode.FAIL);
        }else {
            responseMessage.setCode(ResponseCode.SUCCESS);
            responseMessage.setContentObject(userInfo);
        }
        response.write(responseMessage);
    }

    /**
    * @Author 索半斤
    * @Description 更新用户信息
    * @Date 16:13 2022/11/26
    * @Param [user, request, response]
    * @return zzuli.zw.main.model.protocol.ResponseMessage
    **/
    @RequestMapping(request = Router.UPDATE_USER_INFO)
    public ResponseMessage updateUserInfoRequest(@ParameterName("user")User user,
                                                 RequestParameter request,
                                                 ResponseParameter response){
        int i = userService.updateUserInfoByAccount(user);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequest(request.getRequest());
        if (i == -1){
            responseMessage.setCode(ResponseCode.FAIL);
        }else{
            User userInfo = userService.findDetailUserInfo(user.getId());
            responseMessage.setCode(ResponseCode.SUCCESS);
            responseMessage.setContentObject(userInfo);
        }
        return responseMessage;
    }

    /**
    * @Author 索半斤
    * @Description 更新用户头像
    * @Date 14:50 2022/11/26
    * @Param [image, id, account, fileName, request, response]
    * @return void
    **/
    @RequestMapping(request = Router.UPDATE_USER_HEADER)
    public void updateUserHead(@ParameterName("image")String image,
                                          @ParameterName("user")int id,
                                          @ParameterName("account")String account,
                                          @ParameterName("fileName")String fileName,
                                          RequestParameter request,
                                          ResponseParameter response) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequest(request.getRequest());
        if (image == null || fileName == null){
            responseMessage.setRequest(request.getRequest());
            responseMessage.setContent("更新失败，请稍后重试!");
            responseMessage.setCode(ResponseCode.FAIL);
            response.write(responseMessage);
            return;
        }
        //将Base64格式转换为byte数组
        byte[] decode = FileUtil.getUnZipBytesFromBase64(image);
        String hex = MD5.create().digestHex(decode);
        FileInfo fileInfo = fileInfoService.findFileInfo(hex);
        String filePath;
        if (fileInfo != null && fileInfo.getFilePath() != null){
            filePath = fileInfo.getFilePath();
        }else {
            filePath = UploadUtil.uploadToDefaultPath(decode,fileName);
        }
        int i = userService.updateUserHead(filePath, id);
        if (i == -1){
            responseMessage.setCode(ResponseCode.FAIL);
            response.write(responseMessage);
            return;
        }
        User user = new User();
        user.setId(id);
        user.setAccount(account);
        user.setHeaderPicture(filePath);
        responseMessage.setContentObject(user);
        responseMessage.setCode(ResponseCode.SUCCESS);
        response.write(responseMessage);
        //向其他在线好友广播头像更新的消息
        ResponseMessage broadcastResponse = new ResponseMessage();
        broadcastResponse.setRequest(Router.UPDATE_FRIEND_IMAGE);
        broadcastResponse.setContentObject(user);
        broadcastResponse.setCode(ResponseCode.SUCCESS);
        request.broadcast().broadcast(broadcastResponse,id);
    }

    /**
    * @Author 索半斤
    * @Description 照片墙上传照片
    * @Date 14:50 2022/11/26
    * @Param [request, response, userId, fileName, file]
    * @return zzuli.zw.main.model.protocol.ResponseMessage
    **/
    @RequestMapping(Router.UPLOAD_USER_PHOTO)
    public ResponseMessage uploadPhoto(RequestParameter request,
                                       ResponseParameter response,
                                       @ParameterName("userId")int userId,
                                       @ParameterName("fileName")String fileName,
                                       @ParameterName("file")String file) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequest(request.getRequest());
        if (file == null || fileName == null){
            responseMessage.setRequest(request.getRequest());
            responseMessage.setContent("上传失败，请稍后重试!");
            responseMessage.setCode(ResponseCode.FAIL);
            return responseMessage;
        }
        //将Base64格式转换为byte数组
        byte[] decode = FileUtil.getUnZipBytesFromBase64(file);
        String hex = MD5.create().digestHex(decode);
        FileInfo fileInfo = fileInfoService.findFileInfo(hex);
        String filePath;
        if (fileInfo != null && fileInfo.getFilePath() != null){
            filePath = fileInfo.getFilePath();
        }else {
            filePath = UploadUtil.uploadToDefaultPath(decode,fileName);
        }
        int insert = photoWallService.insertOnePhoto(filePath,userId);
        if (insert == -1){
            responseMessage.setRequest(request.getRequest());
            responseMessage.setContent("上传失败,请稍后重试!");
            responseMessage.setCode(ResponseCode.FAIL);
            return responseMessage;
        }
        PhotoWall userPhotoWall = photoWallService.findUserPhotoWall(userId);
        responseMessage.setRequest(request.getRequest());
        responseMessage.setContentObject(userPhotoWall);
        responseMessage.setCode(ResponseCode.SUCCESS);
        return responseMessage;
    }
}
