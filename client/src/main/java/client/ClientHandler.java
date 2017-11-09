package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import common.protocol;

public class ClientHandler extends SimpleChannelInboundHandler<protocol> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, protocol proto) {
        if (!DataHandler.getInstance().isChanSet())
            DataHandler.getInstance().setChannel(ctx.channel());
        System.out.print(proto.data);
        DataHandler.getInstance().writeDisplay(proto);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.print("Server has shutdown, press enter to leave the client\r\n");
        DataHandler.getInstance().setServerDisconnect(true);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
