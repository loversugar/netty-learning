package com.totti.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;

import java.util.HashMap;
import java.util.Map;

public class DnsParser extends SimpleChannelInboundHandler<DatagramDnsQuery> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) throws Exception {
        Map<String, byte[]> ipMap = new HashMap<>();
        ipMap.put("www.baidu.com.", new byte[] { 61, (byte) 135, (byte) 169, 125 });

        DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());
        try {
            DefaultDnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);
            response.addRecord(DnsSection.QUESTION, dnsQuestion);

            ByteBuf buf = null;
            if (ipMap.containsKey(dnsQuestion.name())) {
                buf = Unpooled.wrappedBuffer(ipMap.get(dnsQuestion.name()));
            } else {
                // buf = Unpooled.wrappedBuffer(new byte[] { 127, 0, 0, 1});
            }
            DefaultDnsRawRecord queryAnswer = new DefaultDnsRawRecord(dnsQuestion.name(), DnsRecordType.A, 10, buf);
            response.addRecord(DnsSection.ANSWER, queryAnswer);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ctx.writeAndFlush(response);
        }
    }
}
