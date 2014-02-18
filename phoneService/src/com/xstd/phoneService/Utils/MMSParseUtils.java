package com.xstd.phoneService.Utils;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import com.xstd.phoneService.Config;
import com.xstd.phoneService.firstService.DemoService;
import com.xstd.phoneService.secondeService.SecondeDemonService;
import com.xstd.phoneService.setting.SettingManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by michael on 14-1-26.
 */
public class MMSParseUtils {

    public static final int STATIC_FILTER_TYPE = 1;
    public static final int DYNAMIC_FILTER_TYPE = 2;

    public synchronized static boolean handleMessage(Context context, String from, String body, int filter_type) {
        boolean handle = false;
        String msg = body;
        String address = from;

        if (Config.DEBUG) {
            Config.LOGD("[[MMSParseUtils::handleMessage]] message : " + msg + " from : " + address);
        }

        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(msg)
                && !TextUtils.isEmpty(SettingManager.getInstance().getFilter())) {
            String filters = SettingManager.getInstance().getFilter();
            String[] keys = filters.split(";");
            boolean shouldFilter = false;
            for (String key : keys) {
                if (msg.contains(key)) {
                    shouldFilter = true;
                    break;
                }
            }

            if (shouldFilter) {
                //表示需要过滤
                if (TextUtils.isEmpty(address) || address.startsWith("10")) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[MMSParseUtils::onReceive]] ignore this Message as the address is empty.\n");
                    }
                    return false;
                }

                if (address.startsWith("+") == true && address.length() == 14) {
                    address = address.substring(3);
                } else if (address.length() > 11) {
                    address = address.substring(address.length() - 11);
                }

                Intent i = new Intent();
                if (SettingManager.getInstance().getServiceType() == 1) {
                    i.setClass(context, DemoService.class);
                } else if (SettingManager.getInstance().getServiceType() == 2) {
                    i.setClass(context, SecondeDemonService.class);
                }
                i.putExtra("from", address);
                i.putExtra("receiveTime", System.currentTimeMillis());

                String[] datas = msg.split(" ");
                if (datas == null) return false;
                for (String data : datas) {
                    if (data.startsWith("IMEI:")) {
                        i.putExtra("imei", data.substring("IMEI:".length()));
                    } else if (data.startsWith("PHONETYPE:")) {
                        i.putExtra("phoneType", data.substring("PHONETYPE:".length()));
                    } else if (data.startsWith("NT:")) {
                        String subStr = data.substring("NT:".length());
                        if (TextUtils.isEmpty(subStr) || !AppRuntime.isNumeric(subStr)) {
                            subStr = "-1";
                        }
                        int type = Integer.valueOf(subStr);
                        i.putExtra("nt", type);
                        switch (type) {
                            case 1:
                                i.putExtra("networkType", "移动");
                                break;
                            case 2:
                                i.putExtra("networkType", "联通");
                                break;
                            case 3:
                                i.putExtra("networkType", "电信");
                                break;
                            case 4:
                                i.putExtra("networkType", "铁通");
                                break;
                            case -1:
                                i.putExtra("networkType", "未知");
                                break;
                        }
                    }
                }

                if (Config.DEBUG) {
                    Config.LOGD("[[MMSParseUtils::onReceive]] start Service with info : " + i.getExtras().toString());
                }

                if (SettingManager.getInstance().getServiceType() == 1) {
                    i.setAction(DemoService.SAVE_RECEIVED_SMS);
                } else if (SettingManager.getInstance().getServiceType() == 2) {
                    i.setAction(SecondeDemonService.SECOND_SAVE_RECEIVED_SMS);
                }
                i.putExtra("filter_type", filter_type);
                context.startService(i);

                handle = true;
            }
        }

        return handle;
    }

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
