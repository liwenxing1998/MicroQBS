package com.phlink.biz.core.message.configuration;import com.phlink.bus.common.Constants;import lombok.extern.slf4j.Slf4j;import org.springframework.amqp.core.*;import org.springframework.amqp.rabbit.annotation.EnableRabbit;import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;import org.springframework.amqp.rabbit.connection.ConnectionFactory;import org.springframework.amqp.rabbit.core.RabbitAdmin;import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;import org.springframework.amqp.rabbit.core.RabbitTemplate;import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;import org.springframework.amqp.support.converter.MessageConverter;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.messaging.converter.MappingJackson2MessageConverter;import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;@Slf4j@EnableRabbit@Configurationpublic class RabbitQueueConfiguration implements RabbitListenerConfigurer {    /**     * 发送到该队列的message会在一段时间后过期进入到delay_process_queue     * 每个message可以控制自己的失效时间     */    public final static String DELAY_QUEUE_PER_MESSAGE_TTL_NAME = "delay_queue_per_message_ttl";    /**     * 发送到该队列的message会在一段时间后过期进入到delay_process_queue     * 队列里所有的message都有统一的失效时间     */    public final static String DELAY_QUEUE_PER_QUEUE_TTL_NAME = "delay_queue_per_queue_ttl";    public final static int QUEUE_EXPIRATION = 4000;    /**     * message失效后进入的队列，也就是实际的消费队列     */    public final static String DELAY_PROCESS_QUEUE_NAME = "delay_process_queue";    /**     * DLX     */    public final static String DELAY_EXCHANGE_NAME = "delay_exchange";    /**     * 路由到delay_queue_per_queue_ttl的exchange     */    public final static String PER_QUEUE_TTL_EXCHANGE_NAME = "per_queue_ttl_exchange";    /**     * 创建DLX exchange     *     * @return     */    @Bean    DirectExchange delayExchange() {        return new DirectExchange(DELAY_EXCHANGE_NAME);    }    /**     * 创建per_queue_ttl_exchange     *     * @return     */    @Bean    DirectExchange perQueueTTLExchange() {        return new DirectExchange(PER_QUEUE_TTL_EXCHANGE_NAME);    }    /**     * 创建delay_queue_per_message_ttl队列     *     * @return     */    @Bean    Queue delayQueuePerMessageTTL() {        return QueueBuilder.durable(DELAY_QUEUE_PER_MESSAGE_TTL_NAME)                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX，dead letter发送到的exchange                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key                .build();    }    /**     * 创建delay_queue_per_queue_ttl队列     *     * @return     */    @Bean    Queue delayQueuePerQueueTTL() {        return QueueBuilder.durable(DELAY_QUEUE_PER_QUEUE_TTL_NAME)                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key                .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间                .build();    }    /**     * 创建delay_process_queue队列，也就是实际消费队列     *     * @return     */    @Bean    Queue delayProcessQueue() {        return QueueBuilder.durable(DELAY_PROCESS_QUEUE_NAME)                .build();    }    /**     * 将DLX绑定到实际消费队列     *     * @param delayProcessQueue     * @param delayExchange     * @return     */    @Bean    Binding dlxBinding(Queue delayProcessQueue, DirectExchange delayExchange) {        return BindingBuilder.bind(delayProcessQueue)                .to(delayExchange)                .with(DELAY_PROCESS_QUEUE_NAME);    }    /**     * 将per_queue_ttl_exchange绑定到delay_queue_per_queue_ttl队列     *     * @param delayQueuePerQueueTTL     * @param perQueueTTLExchange     * @return     */    @Bean    Binding queueTTLBinding(Queue delayQueuePerQueueTTL, DirectExchange perQueueTTLExchange) {        return BindingBuilder.bind(delayQueuePerQueueTTL)                .to(perQueueTTLExchange)                .with(DELAY_QUEUE_PER_QUEUE_TTL_NAME);    }    /**     * 定义delay_process_queue队列的Listener Container     *     * @param connectionFactory     * @return     */    @Bean    SimpleMessageListenerContainer processContainer(ConnectionFactory connectionFactory, ProcessReceiver processReceiver) {        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();        container.setConnectionFactory(connectionFactory);        container.setQueueNames(DELAY_PROCESS_QUEUE_NAME); // 监听delay_process_queue        container.setMessageListener(new MessageListenerAdapter(processReceiver));        return container;    }    @Bean    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();        factory.setMessageConverter(new MappingJackson2MessageConverter());        return factory;    }    @Override    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());    }    @Bean    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {        rabbitTemplate.setMessageConverter(new ProtobufMessageConverter());        RabbitMessagingTemplate rabbitMessagingTemplate = new RabbitMessagingTemplate();        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);        return rabbitMessagingTemplate;    }    //queue listener 观察 监听模式 当有消息到达时会通知监听在对应的队列上的监听对象//    @Bean//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();//        factory.setConnectionFactory(connectionFactory);//        factory.setMessageConverter(new ProtobufMessageConverter());//        // worker.setPrefetchCount(5);//指定一个请求能处理多少个消息，如果有事务的话，必须大于等于transaction数量.//        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);//        //MANUAL：将ACK修改为手动确认，避免消息在处理过程中发生异常造成被误认为已经成功消费的假象。//        //worker.setAcknowledgeMode(AcknowledgeMode.MANUAL);//        return factory;//    }    @Bean    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();        factory.setConnectionFactory(connectionFactory);        factory.setMessageConverter(messageConverter);        return factory;    }    @Bean    public MessageConverter messageConverter() {        return new ContentTypeDelegatingMessageConverter(new ContentTypeDelegatingMessageConverter());    }    @Bean    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {        return new RabbitAdmin(connectionFactory);    }    @Bean    Queue queueSmsSender(RabbitAdmin rabbitAdmin) {        Queue queue = new Queue(Constants.MQ_QUEUE_SEND_SMS, true);        rabbitAdmin.declareQueue(queue);        return queue;    }    @Bean    TopicExchange exchange(RabbitAdmin rabbitAdmin) {        TopicExchange topicExchange = new TopicExchange(Constants.MQ_EXCHANGE_SEND_SMS);        rabbitAdmin.declareExchange(topicExchange);        return topicExchange;    }    @Bean    Binding bindingExchangeEmailSender(Queue queueSmsSender, TopicExchange exchange, RabbitAdmin rabbitAdmin) {        Binding binding = BindingBuilder.bind(queueSmsSender).to(exchange).with(Constants.MQ_ROUTE_SEND_SMS);        rabbitAdmin.declareBinding(binding);        return binding;    }}