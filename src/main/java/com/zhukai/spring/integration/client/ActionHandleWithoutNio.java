package com.zhukai.spring.integration.client;


import com.zhukai.spring.integration.common.HttpParser;
import com.zhukai.spring.integration.utils.JsonUtil;

import java.io.*;
import java.net.Socket;


/**
 * Created by zhukai on 17-1-12.
 */
public class ActionHandleWithoutNio extends ActionHandle {

    private Socket socket;

    public ActionHandleWithoutNio(Socket socket) {
        this.socket = socket;
        try {
            request = HttpParser.parseRequest(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //返回消息,并结束
    protected void respond() {
        PrintStream out = null;
        try {
            if (response == null) {
                return;
            }
            out = new PrintStream(socket.getOutputStream(), true);
            if (InputStream.class.isAssignableFrom(response.getResult().getClass())) {
                int contentLength = ((InputStream) response.getResult()).available();
                response.setHeader("Content-Length", "" + contentLength);
            }
            String httpHeader = HttpParser.parseHttpString(response);
            out.println(httpHeader);

            if (InputStream.class.isAssignableFrom(response.getResult().getClass())) {
                InputStream inputStream = (InputStream) response.getResult();
                int len = inputStream.available();
                if (len <= 1024 * 1024) {
                    byte[] bytes = new byte[len];
                    inputStream.read(bytes);
                    out.write(bytes);
                } else {
                    int byteCount;
                    byte[] bytes = new byte[1024 * 1024];
                    while ((byteCount = inputStream.read(bytes)) != -1) {
                        out.write(bytes, 0, byteCount);
                    }
                }
                inputStream.close();
            } else {
                out.println(JsonUtil.toJson(response.getResult()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
