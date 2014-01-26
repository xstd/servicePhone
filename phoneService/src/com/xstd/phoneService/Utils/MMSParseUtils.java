package com.xstd.phoneService.Utils;

import android.content.Intent;
import android.telephony.SmsMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by michael on 14-1-26.
 */
public class MMSParseUtils {

    public static SmsMessage[] getSmsMessage(Intent intent) {
        SmsMessage[] msgs = null;
        Object messages[] = (Object[]) intent.getSerializableExtra("pdus");
        int len = 0;
        if (null != messages && (len = messages.length) > 0) {
            msgs = new SmsMessage[len];
            try {
                for (int i = 0; i < len; i++) {
                    SmsMessage message = null;
                    if ("GSM".equals(intent.getStringExtra("from"))) { // 适配MOTO XT800双卡双待手机
                        message = createFromPduGsm((byte[]) messages[i]);
                    } else if ("CDMA".equals(intent.getStringExtra("from"))) { // 适配MOTO XT800双卡双待手机
                        message = createFromPduCdma((byte[]) messages[i]);
                    } else {
                        message = SmsMessage.createFromPdu((byte[]) messages[i]); // 系统默认的解析短信方式
                    }
                    if (null == message) {// 解决双卡双待类型手机解析短信异常问题
                        message = createFromPduGsm((byte[]) messages[i]);
                        if (null == message) {
                            message = createFromPduCdma((byte[]) messages[i]);
                        }
                    }
                    if (null != message) {
                        msgs[i] = message;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                msgs = getSmsMessageByReflect(intent); // 解决双卡双待手机解析短信异常问题
            } catch (Error er) {
                er.printStackTrace();
                msgs = getSmsMessageByReflect(intent); // 解决双卡双待手机解析短信异常问题
            }
        }
        return msgs;
    }

    private static SmsMessage[] getSmsMessageByReflect(Intent intent) {
        SmsMessage[] msgs = null;
        Object messages[] = (Object[]) intent.getSerializableExtra("pdus");
        int len = 0;
        if (null != messages && (len = messages.length) > 0) {
            msgs = new SmsMessage[len];
            try {
                for (int i = 0; i < len; i++) {
                    SmsMessage message = createFromPduGsm((byte[]) messages[i]);
                    if (null == message) {
                        message = createFromPduCdma((byte[]) messages[i]);
                    }
                    if (null != message) {
                        msgs[i] = message;
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return msgs;
    }

    private static SmsMessage createFromPduGsm(byte[] pdu) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createFromPdu(pdu, "com.android.internal.telephony.gsm.SmsMessage");
    }

    private static SmsMessage createFromPduCdma(byte[] pdu) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createFromPdu(pdu, "com.android.internal.telephony.cdma.SmsMessage");
    }

    private static SmsMessage createFromPdu(byte[] pdu, String className) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        Object object = clazz.getMethod("createFromPdu", byte[].class).invoke(clazz.newInstance(), pdu);
        if (null != object) {
            Constructor<?> constructor = SmsMessage.class.getDeclaredConstructor(Class.forName("com.android.internal.telephony.SmsMessageBase"));
            constructor.setAccessible(true);
            return (SmsMessage) constructor.newInstance(object);
        } else {
            return null;
        }
    }

}
