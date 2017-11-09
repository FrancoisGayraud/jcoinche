package common;

import io.netty.channel.Channel;
import common.fileWriter;

public class protocolWriter {
    private static protocolWriter instance = new protocolWriter();

    public void writeProtocol(Channel chan, protocol proto) {
        fileWriter logWriter = new fileWriter("./jcoinche.log");
        logWriter.writeFileAndCreateIfNeeded(proto.data);
        chan.writeAndFlush(proto);
    }

    public static protocolWriter getInstance() {
        return instance;
    }
}
