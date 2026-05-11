package online.kbpf.dg_lab.client.webSocketServer;


import online.kbpf.dg_lab.client.entity.DGStrength;
import online.kbpf.dg_lab.client.entity.clientInfo;
import com.google.gson.Gson;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import net.minecraft.client.Minecraft;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;


import static online.kbpf.dg_lab.client.Dg_labClient.waveformMap;


public class webSocketServer extends WebSocketServer {

    private boolean isRunning = false;

    private boolean isConnected = false;

    private WebSocket client;

    private clientInfo clientInfo = new clientInfo("bind", "1234-123456789-12345-12345-00", "", "targetId");

    private DGStrength dgStrength = new DGStrength();



    public webSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (!isConnected) {
            dgStrength = new DGStrength();
            isConnected = true;
            client = conn;
            client.send(new Gson().toJson(clientInfo, clientInfo.class));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("==="));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("作者不对使用此模组造成的人身伤害和精神伤害负责"));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("使用此模组请自行注意人身安全"));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("==="));

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (isConnected) {
                            clientInfo.setType("heartbeat");
                            clientInfo.setMessage("200");
                            conn.send(new Gson().toJson(clientInfo, clientInfo.class));


                    }
                }
            }, 0, 60000);
        } else {
            conn.send("{\"type\":\"error\",\"message\":\"400\"}");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (conn.equals(client)) {
            isConnected = false;
            client = null;
            clientInfo = new clientInfo("bind", "1234-123456789-12345-12345-00", "", "targetId");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        clientInfo tmp = new Gson().fromJson(message, clientInfo.class);

        if (tmp.getMessage().equals("DGLAB") && tmp.getType().equals("bind") && tmp.getClientId().equals("1234-123456789-12345-12345-01") && tmp.getTargetId().equals(clientInfo.getClientId())) {
            clientInfo = tmp;
            clientInfo.setMessage("200");
            client.send(new Gson().toJson(clientInfo, clientInfo.class));
        } else if (tmp.getType().equals("msg")) {
            String message1 = tmp.getMessage();
            StringBuilder number = new StringBuilder();
            int count = 0;
            boolean iscount = false;
            for (char ch : message1.toCharArray()) {
                if (Character.isDigit(ch)) {
                    iscount = true;
                    number.append(ch);
                } else if (iscount) {
                    switch (count) {
                        case 0:
                            dgStrength.setAStrength(Integer.parseInt(number.toString()));
                            break;
                        case 1:
                            dgStrength.setBStrength(Integer.parseInt(number.toString()));
                            break;
                        case 2:
                            dgStrength.setAMaxStrength(Integer.parseInt(number.toString()));
                            break;
                        case 3:
                            dgStrength.setBMaxStrength(Integer.parseInt(number.toString()));
                            break;
                    }
                    number = new StringBuilder();
                    count++;
                }
            }
            int Number = Integer.parseInt(number.toString());
            if (Number == 405) {
                if (Minecraft.getInstance().player != null)

                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("发送的消息长度超过1950").setStyle(Style.EMPTY.withColor(0xFF0000)));
            }

            else dgStrength.setBMaxStrength(Number);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        isRunning = true;
    }

    public void sendStrengthToClient(int value, int mode, int A1or2B) {
        if (isConnected) {
            clientInfo.setMessage("strength-" + A1or2B + '+' + mode + '+' + value);
            clientInfo.setType("msg");
            client.send(new Gson().toJson(clientInfo, clientInfo.class));
        }
    }

    public void  setDelayTime(int A, int B) {
        dgStrength.setADelayTime(A);
        dgStrength.setBDelayTime(B);
    }
    public void  setADelayTime(int A) {
        dgStrength.setADelayTime(A);
    }
    public void  setBDelayTime(int B) {
        dgStrength.setBDelayTime(B);
    }

    public void setStrength(DGStrength DGStrength) {
        dgStrength = DGStrength;
    }

    public void sendStrength() {
        if (isConnected) {
            clientInfo.setType("msg");
            clientInfo.setMessage("strength-1+2+" + Math.min(dgStrength.getAStrength(), dgStrength.getAMaxStrength()));
            client.send(new Gson().toJson(clientInfo, clientInfo.class));

            clientInfo.setMessage("strength-2+2+" + Math.min(dgStrength.getBStrength(), dgStrength.getBMaxStrength()));
            client.send(new Gson().toJson(clientInfo, clientInfo.class));
        }
    }

    public DGStrength getStrength() {
        return dgStrength;
    }

    public void CleanFrequency(int A1orB2) {
        if(isConnected) {
            clientInfo.setType("msg");
            if(A1orB2 == 1) clientInfo.setMessage("clear-1");
            else if(A1orB2 == 2) clientInfo.setMessage("clear-2");
            client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));
        }
    }




    public void sendDgWaveform(int Damage2orHealth3, boolean cleanPrevious, int A1orB2) {

        if (isConnected) {
            if (cleanPrevious) CleanFrequency(A1orB2);


            clientInfo.setType("msg");

            if (Damage2orHealth3 == 2) {
                if (A1orB2 == 1) {
                    clientInfo.setMessage("pulse-A:[" + waveformMap.get("ADamage").getWaveform() + "]");
                    client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));

                } else if (A1orB2 == 2) {
                    clientInfo.setMessage("pulse-B:[" + waveformMap.get("BDamage").getWaveform() + "]");
                    client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));
                }
            } else if (Damage2orHealth3 == 3) {
                if (A1orB2 == 1) {
                    clientInfo.setMessage("pulse-A:[" + waveformMap.get("AHealing").getWaveform() + "]");
                    client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));
                } else if (A1orB2 == 2) {
                    clientInfo.setMessage("pulse-B:[" + waveformMap.get("BHealing").getWaveform() + "]");
                    client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));
                }
            }
        }
    }


    public void sendDGWaveForm(String message, int A1orB2){
        if(isConnected){
            clientInfo.setType("msg");
            if(A1orB2 == 1) {
                CleanFrequency(1);
                clientInfo.setMessage("pulse-A:[" + message + "]");
            }
            else {
                CleanFrequency(2);
                clientInfo.setMessage("pulse-B:[" + message + "]");
            }
            client.send(new Gson().toJson(clientInfo, online.kbpf.dg_lab.client.entity.clientInfo.class));
        }
    }





    public boolean getState() {
        return isRunning;
    }

    public boolean getConnected() {
        return isConnected;
    }
}
