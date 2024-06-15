package org.chiu.micro.search.mq.handler;


import org.chiu.micro.search.dto.BlogEntityDto;
import org.chiu.micro.search.lang.Const;
import org.chiu.micro.search.rpc.BlogHttpService;
import org.chiu.micro.search.constant.BlogOperateEnum;
import org.chiu.micro.search.constant.BlogOperateMessage;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
public abstract sealed class BlogIndexSupport permits
        CreateBlogIndexHandler,
        RemoveBlogIndexHandler,
        UpdateBlogIndexHandler {

    protected final StringRedisTemplate redisTemplate;

    protected final BlogHttpService blogHttpService;

    protected BlogIndexSupport(StringRedisTemplate redisTemplate,
                               BlogHttpService blogHttpService) {
        this.redisTemplate = redisTemplate;
        this.blogHttpService = blogHttpService;
    }

    public abstract boolean supports(BlogOperateEnum blogOperateEnum);
    protected abstract void elasticSearchProcess(BlogEntityDto blog);

    @SneakyThrows
    public void handle(BlogOperateMessage message, Channel channel, Message msg) {
        String createUUID = msg.getMessageProperties().getHeader(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY);
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(Const.CONSUME_MONITOR.getInfo()  + createUUID))) {
            try {
                Long blogId = message.getBlogId();
                BlogEntityDto blogEntity = blogHttpService.findById(blogId);

                elasticSearchProcess(blogEntity);
                //手动签收消息
                //false代表不是批量签收模式
                channel.basicAck(deliveryTag, false);
                redisTemplate.delete(Const.CONSUME_MONITOR.getInfo() + createUUID);
            } catch (Exception e) {
                log.error("consume failure", e);
                channel.basicNack(deliveryTag, false, true);
            }
        } else {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
