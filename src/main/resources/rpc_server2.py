#!/usr/bin/env python
import pika
import time
user_pwd = pika.PlainCredentials('guest', 'guest')
connection = pika.BlockingConnection(pika.ConnectionParameters(
    host='localhost', credentials=user_pwd))

channel = connection.channel()

# channel.queue_declare(queue='rpc_queue')
# channel.queue_declare()


def on_request(ch, method, props, body):

    print " [.] get msg = %s" % (body,)
    print " reply_to == ", props.reply_to, props.correlation_id

    ch.basic_publish(exchange='',
                     routing_key=props.reply_to,
                     properties=pika.BasicProperties(correlation_id = \
                                                         props.correlation_id),
                     body=str(body+"_result2"))
    ch.basic_ack(delivery_tag=method.delivery_tag)

channel.basic_qos(prefetch_count=1)
channel.basic_consume(on_request, queue='app.request')

print " [x] Awaiting RPC requests"
channel.start_consuming()
