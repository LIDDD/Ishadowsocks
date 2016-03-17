package cn.lid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 从http://freeshadowsocks.cf/上取得vpn的配置信息并更新Shadowsocks的gui-config.json文件.
 */
public class App {
    public static void main(String[] args) {
        Document doc = null;
        try {
            // 获取配置信息的地址
            doc = Jsoup.connect("http://freeshadowsocks.cf/").get();
            Elements srvs = doc.select("h4:contains(服务器地址:)");
            Elements ports = doc.select("h4:contains(端口:)");
            Elements pwds = doc.select("h4:contains(密码:)");
            Elements ens = doc.select("h4:contains(加密方式:)");

            // 组装配置文件JSON
            JSONObject jo = new JSONObject();
            JSONArray congfig = new JSONArray();
            for (int i = 0; i < srvs.size(); i++) {
                JSONObject each = new JSONObject();
                each.put("server", srvs.get(i).text().split(":")[1]);
                each.put("server_port", ports.get(i).text().split(":")[1]);
                if(pwds.get(i).text().split(":").length == 2) {
                    each.put("password", pwds.get(i).text().split(":")[1]);
                } else {
                    each.put("password", "");
                }
                each.put("method", ens.get(i).text().split(":")[1]);
                each.put("remarks", "");
                congfig.add(each);
            }

            jo.put("configs", congfig);
            jo.put("strategy", "com.shadowsocks.strategy.ha");
            jo.put("index", -1);
            jo.put("global", false);
            jo.put("enabled", true);
            jo.put("shareOverLan", false);
            jo.put("isDefault", false);
            jo.put("localPort", 1080);
            jo.put("pacUrl", null);
            jo.put("useOnlinePac", false);
            jo.put("availabilityStatistics", false);

            // 更新配置文件地址
            FileOutputStream fos=new FileOutputStream("D:\\02-DEV\\04-TOOLS\\Shadowsocks\\gui-config.json");
            fos.write(jo.toString().getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
