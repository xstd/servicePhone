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
    private static final int VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema receiveSchema = new Schema(VERSION, PACKAGE_NAME_RECEIVE);
        Schema sendSchema = new Schema(VERSION, PACKAGE_NAME_SEND);
        Schema statusSchema = new Schema(VERSION, PACKAGE_NAME_STATUS);

        generateSMSSentLog(sendSchema);
        generateSMSReceivedLog(receiveSchema);
        generateSMSStatusLog(statusSchema);

        new DaoGenerator().generateAll(sendSchema, "../../src");
        new DaoGenerator().generateAll(receiveSchema, "../../src");
        new DaoGenerator().generateAll(statusSchema, "../../src");
    }

    private static void generateSMSStatusLog(Schema schema) {
        Entity note = schema.addEntity("SMSStatus");

        note.addIdProperty().autoincrement();
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

        note.addIdProperty().autoincrement();
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

        note.addIdProperty().autoincrement();
        note.addStringProperty("from").notNull().primaryKey();
        note.addStringProperty("imei").notNull();
        note.addStringProperty("phoneType").notNull();
        note.addStringProperty("networkType");
        note.addLongProperty("receiveTime").notNull();
    }
}
