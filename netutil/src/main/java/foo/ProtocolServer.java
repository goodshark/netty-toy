package foo;

import foo.handlers.ProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.redis.*;

import java.net.InetSocketAddress;

public class ProtocolServer {
    public static void main(String[] args) throws Exception {
        new ProtocolServer().start();
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(1234))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // input
                            socketChannel.pipeline().addLast(new RedisDecoder());
                            socketChannel.pipeline().addLast(new RedisBulkStringAggregator());
                            socketChannel.pipeline().addLast(new RedisArrayAggregator());
                            socketChannel.pipeline().addLast(new ProtocolHandler());
                            // output
                            socketChannel.pipeline().addFirst(new RedisEncoder());
                        }

                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
