package basic.kafka

import basic.common.PropertiesUtil
import kafka.api.{OffsetRequest, PartitionOffsetRequestInfo, TopicMetadataRequest}
import kafka.common.TopicAndPartition
import kafka.consumer.{ConsumerConfig, SimpleConsumer}

import scala.util.Random

/**
  * KafkaHelper
  *
  * @author wuguolin
  */
class KafkaHelper(host: String, port: Int, topic: String, time: Long, groupId: String) {

    /**
      * Get offset by specify time of each partition
      *
      * @return
      */
    def getPartitionAndOffsetByTime: Map[TopicAndPartition, Long] = {

        //According to search topicMeta find each host who has partition
        val kafka_consumer = new SimpleConsumer(host, port, ConsumerConfig.SocketTimeout,
            ConsumerConfig.SocketBufferSize, groupId)

        val topicMetaData = new TopicMetadataRequest(List(topic), 0)
        val topicMetaDataResponse = kafka_consumer.send(topicMetaData)

        val partitionHost = topicMetaDataResponse.topicsMetadata.flatMap(x => {
            x.partitionsMetadata.map(y => {
                (y.partitionId, y.leader.get.host)
            })
        })
        //get each partition with offset by specify time
        partitionHost.map(x => {
            val partition = x._1
            val kafka_consumer = new SimpleConsumer(x._2, port, ConsumerConfig.SocketTimeout,
                ConsumerConfig.SocketBufferSize, groupId)

            val topicAndPartition = TopicAndPartition(topic, partition)

            val requestInfo = Map[TopicAndPartition, PartitionOffsetRequestInfo](
                topicAndPartition -> PartitionOffsetRequestInfo(time, 1))

            val request = new OffsetRequest(requestInfo)
            val response = kafka_consumer.getOffsetsBefore(request)
            if (response.hasError) {
                print("Error fetching data Offset Data the Broker. Reason: " + response.error(this.topic + "," + partition))
            }
            val offsets = response.offsetsGroupedByTopic(this.topic)(topicAndPartition).offsets
            var offset = 0L
            if (offsets.nonEmpty) {
                offset = offsets.head
            }
            (topicAndPartition, offset)
        }).toMap
    }
}


/**
  * kafka helper
  */
object KafkaHelper {

    implicit def StringToInt(value: String): Int = Integer.parseInt(value)

    /**
      * Get properties from config file
      *
      * @param time specify time
      * @return
      */
    def apply(time: Long): KafkaHelper = {
        val topic = PropertiesUtil.getProperty("topic")
        val groupId = PropertiesUtil.getProperty("group_id")
        val propertyName = PropertiesUtil.getProperty("bootstrap.servers")
        val bootstrap = PropertiesUtil.getProperty(propertyName)
        val hosts = bootstrap.split(",")
        // random select broker
        val index = Random.nextInt(hosts.length)
        val hostAndPort = hosts(index).split(":")
        new KafkaHelper(hostAndPort(0), hostAndPort(1), topic, time, groupId)
    }

    /**
      * Instance by hand
      *
      * @param host    host
      * @param port    port
      * @param topic   topicName
      * @param time    specify time
      * @param groupId groupId
      * @return
      */
    def apply(host: String, port: Int, topic: String, time: Long, groupId: String): KafkaHelper =
        new KafkaHelper(host, port, topic, time, groupId)

}
