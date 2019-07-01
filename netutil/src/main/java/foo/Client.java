package foo;

import foo.handlers.ClientEchoHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class Client {
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 1234))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientEchoHandler());
                        }
                    });

            // sync connect and wait to be done
            /*ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();*/

            // async connect and add listener to inform connect success, at last wait to be done
            ChannelFuture f = b.connect();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("connect is ok ...");
                    } else {
                        System.out.println("connect failed ...");
                        future.cause().printStackTrace();
                    }
                }
            });
            f.channel().closeFuture().sync();

            // shutdown the EventLoopGroup and wait to be done
            Future<?> future = group.shutdownGracefully();
            future.syncUninterruptibly();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client().start();


        /*ByteBuf buf = Unpooled.copiedBuffer("test only".getBytes());
        buf.readSlice(0);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.writeInbound(buf.retain());*/

    }
}
