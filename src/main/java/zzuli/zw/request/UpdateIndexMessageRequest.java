package zzuli.zw.request;

import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.ChatInfoService;
import zzuli.zw.service.ChatInfoServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.IOException;

/**
 * @ClassName UpdateIndexMessageRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:41
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_INDEX_MESSAGE*/"")
public class UpdateIndexMessageRequest extends BaseRequest {
    private  ChatInfoService chatInfoService = AopUtils.aop(ChatInfoServiceImpl.class, ChatInfoService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
       /* User user = MapToObject.MapToUser(request.getRequestData());
        List<IndexMessageInfo> indexChatInfo = chatInfoService.findIndexChatInfo(user.getUsername());
        JsonResult<IndexMessageInfo> jsonResult = new JsonResult<>();
        jsonResult.setStatus(MessageType.REQUEST_UPDATE_INDEX_MESSAGE);
        jsonResult.setList(indexChatInfo);
        response.write(jsonResult);*/
    }
}
