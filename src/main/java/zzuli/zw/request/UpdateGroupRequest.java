package zzuli.zw.request;

import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.GroupInfoService;
import zzuli.zw.service.GroupInfoServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.IOException;

/**
 * @ClassName UpdateGroupRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:31
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_GROUP*/"")
public class UpdateGroupRequest extends BaseRequest {
    GroupInfoService groupInfoService = AopUtils.aop(GroupInfoServiceImpl.class, GroupInfoService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        /*User user = MapToObject.MapToUser(request.getRequestData());
        List<IndexGroupInfo> indexGroupInfo = groupInfoService.findIndexGroupInfo(user.getUsername());
        JsonResult<IndexGroupInfo> jsonResult = new JsonResult<>();
        jsonResult.setStatus(MessageType.REQUEST_UPDATE_GROUP);
        jsonResult.setList(indexGroupInfo);
        response.write(jsonResult);*/

    }
}
