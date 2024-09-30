package com.whitecrow;//package com.whitecrow;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Data
//@Slf4j
//public class test {
//    /**
//     * 页面初始化，获取奖品列表
//     *
//     * @return
//     */
//    @RequestMapping("/getAwardsList")
//    @ResponseBody
//    public Object getAwardsList(String phoneNo) {
//        JSONObject resp=new JSONObject();
//        //API_Aopuserspthreepartcheck获得身份号
//        String certCode = "";
//        String developDepartId = "";
//        Map<String, Object> param = new HashMap<>(16);
//        param.put("account", phoneNo);
//        if (StrUtil.isEmpty(phoneNo)) {
//            resp.put("code","1");
//            resp.put("msg","参数缺失！");
//            return resp;
//        }
//        try {
//            JSONObject response = interfaceDubboApi.callJson("Aopuserspthreepartcheck", param);
//            if (response.getJSONObject("Head").getString("status").equals("0")) {
//                JSONObject body = response.getJSONObject("Body");
//                JSONObject custInfo = body.getJSONObject("custInfo");
//                certCode = custInfo.getString("certCode");
//
//                JSONObject userInfo = body.getJSONObject("userInfo");
//                developDepartId = userInfo.getString("developDepartId");
//            }
//            log.info("-------------getAwardsList-------certCode:{}", certCode);
//        } catch (Exception e) {
//            log.info("Aopuserspthreepartcheck异常:{}", e.toString());
//        }
//        if (StrUtil.isEmpty(certCode) || StrUtil.isEmpty(developDepartId)) {
//            resp.put("code","1");
//            resp.put("msg","未查到用户信息，请稍后再试！");
//            return resp;
//        }
//        String userId = "";
//        String openDate = "";
//        String productId = "";//用户主套餐
//        boolean isJS = false;//默认异网
//        try {
//            param.put("identification", certCode);
//            JSONObject response = interfaceDubboApi.callJson("userServicesInUse", param);//证件查询在用用户服务接口
//            JSONObject rsp = response.getJSONObject("USER_SERVICES_IN_USE_RSP").getJSONObject("RSP");
//            log.info("-------------getAwardsList-------userServicesInUse:{}",response);
//            if (rsp.getString("RSP_CODE").equals("0000")) {
//                JSONArray data = rsp.getJSONArray("DATA");
//                JSONArray userInfo = data.getJSONObject(0).getJSONArray("USER_INFO");
//                for (int i = 0; i < userInfo.size(); i++) {
//                    JSONObject obj = userInfo.getJSONObject(i);
//                    if (obj.getString("NET_TYPE_CODE").equals("50")) {
//                        String serialNumber=obj.getString("SERIAL_NUMBER");
//                        String productName=obj.getString("PRODUCT_NAME");
//                        if (!serialNumber.equals(phoneNo)&&!isJS) {
//                            if(checkPhoneStatus(serialNumber,productName)){
//                                isJS = true;//本网
//                            }
//                        }
//                        if(serialNumber.equals(phoneNo)){
//                            userId = obj.getString("USER_ID");
//                            openDate = obj.getString("IN_DATE");
//                            productId = obj.getString("PRODUCT_ID");
//                        }
//                    }
//                }
//                log.info("-------------getAwardsList-------userId:{}，openDate:{},productId:{}", userId, openDate, productId);
//            }
//        } catch (Exception e) {
//            log.info("userServicesInUse 异常:{} ", e.getMessage());
//        }
//        if (StrUtil.isEmpty(userId) || StrUtil.isEmpty(openDate) || StrUtil.isEmpty(productId)) {
//            resp.put("code","1");
//            resp.put("msg","未查到证件信息，请稍后再试！");
//            return resp;
//        }
//
//        String activityId = "";
//        //判断是否新用户
//        boolean isNewUser = isNewUser(phoneNo, openDate, developDepartId);
//        log.info("-------------getAwardsList-------phoneNo:{},isNewUser:{}",phoneNo,isNewUser);
//        if (isNewUser) {//新用户
//
//            if (isJS) {//本网
//                activityId = ACTIVITY_BW;
//            } else {//异网
//                activityId = ACTIVITY_YW;
//            }
//
//            try {
//                if (StrUtil.isNotEmpty(activityId)) {
//                    String rightActiveInfo = rightCenterCacheManager.getActiveInfoById(activityId);
//                    RightActiveDto rightActiveDto ;
//                    if (StringUtils.isNotEmpty(rightActiveInfo)) {
//                        rightActiveDto = JSON.parseObject(rightActiveInfo, RightActiveDto.class);
//                    } else {
//                        rightActiveDto = wechatRightCenterService.findRightActiveById(Integer.valueOf(activityId));
//                    }
//                    if(rightActiveDto==null){
//                        resp.put("code","1");
//                        resp.put("msg","活动未配置！");
//                        return resp;
//                    }
//                    JSONObject jsonBody = JSONObject.parseObject(JSONObject.toJSONString(rightActiveDto));
//                    log.info("-------------getAwardsList-------rightActiveDto:{}",JSONObject.toJSONString(rightActiveDto));
//                    //判断是否已订购产品
//                    Map<String, Object> paramMap = new HashMap<String,Object>() ;
//                    paramMap.put("phoneNo", phoneNo);
//                    paramMap.put("openId", userId);
//                    paramMap.put("beginDate", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -10),DatePattern.NORM_DATE_FORMAT));
//                    paramMap.put("endDate", DateUtil.now());
//                    Page<Map<String,Object>> page = new Page<>(1, 10);
//                    page.setDesc("orderNo");
//                    IPage<Map<String, Object>> list = orderInfoResultService.orderInfoQuery(page, paramMap);
//                    log.info("-------------getAwardsList-------orderInfoQuery:{}",list);
//                    if (list != null && CollectionUtil.isNotEmpty(list.getRecords())) {
//                        //获取订单状态
//                        Map<String, Object> orderDetail = list.getRecords().get(0);
//
//                        if(orderDetail.get("orderStatus")==null){
//                            jsonBody.put("userId", userId);
//                            resp.put("data",jsonBody);
//                            resp.put("code","3");
//                            resp.put("msg","订购失败");
//                            return resp;
//                        }
//                        String orderStatus = orderDetail.get("orderStatus").toString();
//                        String skuName = orderDetail.get("skuName").toString();
//                        if(orderStatus.equals("0070")){//订购失败,返回失败原因
//                            String sendDesc = orderDetail.get("sendDesc").toString();
//                            jsonBody.put("userId", userId);
//                            resp.put("data",jsonBody);
//                            resp.put("code","3");
//                            resp.put("msg",skuName+"订购失败，"+sendDesc);
//                            return resp;
//                        }
//                        resp.put("code","2");//进页面立马弹窗
//                        resp.put("msg","已订购产品:"+skuName);
//                        return resp;
//                    }
//
//                    jsonBody.put("userId", userId);
//                    resp.put("code","0");
//                    resp.put("msg","成功");
//                    resp.put("data",jsonBody);
//                    return resp;
//                }
//            } catch (Exception e) {
//                log.info("-------------getAwardsList-------异常:{}", e.toString());
//                resp.put("code","1");
//                resp.put("msg","服务异常,请稍后再试!");
//                return resp;
//            }
//
//        }
//        if (!JedisClusterUtils.tryLock("WO_LUCKYBOX_NOAWARD_" + phoneNo, 30*24*60)) {//存30天
//            resp.put("code","1");
//            resp.put("msg","已参与过抽奖!");
//            return resp;
//        }
//        resp.put("code","1");
//        resp.put("msg","谢谢参与!");
//        return resp;
//    }
//
//
//
//}
