package server;

import common.protocolWriter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import server.game.GameHandler;
import common.protocol;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<protocol> {

    private static EventExecutor executor;
    private static final ChannelGroup channels = new DefaultChannelGroup(executor);

    public static ChannelGroup getChannels() {
        return channels;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        System.out.print(ch.remoteAddress() + " connected\n");
        channels.add(ctx.channel());
        GameHandler.getInstance().pushClient(ch, ch.remoteAddress().toString());
        GameHandler.getInstance().printClient();
        ctx.flush();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        GameHandler.getInstance().deleteClient(ch);
        channels.remove(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, protocol proto) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress inetaddress = socketAddress.getAddress();
        String ip;
        ip = inetaddress.getHostAddress();
        try {
            System.out.print(ip + " " + proto.data);
        }
        finally {
            if (GameHandler.getInstance().checkIfWaiting(ctx.channel())) {
                protocolWriter.getInstance().writeProtocol(ctx.channel(), new protocol("Waiting for someone to disconnect\n"));
            }
            else if (GameHandler.getInstance().getNbPlayer() == 4)
                GameHandler.getInstance().setResponse(proto.data, ctx.channel());
            else {
                protocolWriter.getInstance().writeProtocol(ctx.channel(), new protocol("Waiting for other player to begin game\n"));
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        System.out.println(cause.getMessage());
        cause.printStackTrace();
    }

}