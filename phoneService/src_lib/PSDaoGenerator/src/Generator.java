import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:32
 * To change this template use File | Settings | File Templates.
 */
public class Generator {

    private static final String PACKAGE_NAME_RECEIVE = "com.xstd.phoneService.model.receive";
    private static final String PACKAGE_NAME_SEND = "com.xstd.phoneService.model.send";
    private static final String PACKAGE_NAME_STATUS = "com.xstd.phoneService.model.status";
    private static final String PACKAGE_UPDATE_STATUS = "com.xstd.phoneService.model.update";
    private static final String PACKAGE_REPEAT_LOG = "com.xstd.phoneService.model.repeat";

    private static final int VERSION = 2;

    public static void main(String[] args) throws Exception {
        Schema receiveSchema = new Schema(VERSION, PACKAGE_NAME_RECEIVE);
        Schema sendSchema = new Schema(VERSION, PACKAGE_NAME_SEND);
        Schema statusSchema = new Schema(VERSION, PACKAGE_NAME_STATUS);
        Schema updateStatusSchema = new Schema(VERSION, PACKAGE_UPDATE_STATUS);
        Schema repeatStausSchema = new Schema(VERSION, PACKAGE_REPEAT_LOG);

        generateSMSSentLog(sendSchema);
        generateSMSReceivedLog(receiveSchema);
        generateSMSStatusLog(statusSchema);
        generateSMSUpdateStatusLog(updateStatusSchema);
        generateSMSRepeatStatusLog(repeatStausSchema);

        new DaoGenerator().generateAll(sendSchema, "../../src");
        new DaoGenerator().generateAll(receiveSchema, "../../src");
        new DaoGenerator().generateAll(statusSchema, "../../src");
        new DaoGenerator().generateAll(updateStatusSchema, "../../src");
        new DaoGenerator().generateAll(repeatStausSchema, "../../src");
    }

    private static void generateSMSRepeatStatusLog(Schema schema) {
        Entity note = schema.addEntity("SMSRepeat");

        note.addStringProperty("from").notNull().primaryKey();
        note.addStringProperty("phoneType").notNull();
        note.addStringProperty("imsi").notNull();
        note.addStringProperty("networkType").notNull();
        note.addLongProperty("receiveTime").notNull();
        note.addLongProperty("repeatCount");
    }

    private static void generateSMSStatusLog(Schema schema) {
        Entity note = schema.addEntity("SMSStatus");

//        note.addIdProperty().autoincrement();
        note.addLongProperty("serverID").notNull().primaryKey();
        note.addLongProperty("receviedCount");
        note.addLongProperty("sentCount");
        note.addLongProperty("leaveCount");
        note.addLongProperty("lastSentTime");
        note.addLongProperty("lastReceivedTime");
        note.addLongProperty("cmnetCount");
        note.addLongProperty("unicomCount");
        note.addLongProperty("telecomCount");
        note.addLongProperty("subwayCount");
        note.addLongProperty("unknownCount");
    }

    private static void generateSMSSentLog(Schema schema) {
        Entity note = schema.addEntity("SMSSent");

//        note.addIdProperty().autoincrement();
        note.addStringProperty("from").notNull().primaryKey();
        note.addStringProperty("imei").notNull();
        note.addStringProperty("phoneType").notNull();
        note.addStringProperty("networkType");
        note.addLongProperty("receiveTime").notNull();
        note.addLongProperty("sendTime").notNull();
        note.addStringProperty("sendPhoneNumber").notNull();
        note.addStringProperty("extra");
        note.addStringProperty("extra1");
        note.addStringProperty("extra2");
    }

    private static void generateSMSReceivedLog(Schema schema) {
        Entity note = schema.addEntity("SMSReceived");

//        note.addIdProperty().autoincrement();
        note.addStringProperty("from").notNull().primaryKey();
        note.addStringProperty("imei").notNull();
        note.addStringProperty("phoneType").notNull();
        note.addStringProperty("networkType");
        note.addLongProperty("receiveTime").notNull();
    }

    private static void generateSMSUpdateStatusLog(Schema schema) {
        Entity note = schema.addEntity("SMSUpdateSyncStatus");

        note.addIdProperty().autoincrement();
        note.addLongProperty("updateTime").notNull();
        note.addLongProperty("extra1");
        note.addLongProperty("extra2");
    }
}
