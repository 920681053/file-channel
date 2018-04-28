## FileChannel是什么?

FileChannel是一个连接到文件的通道，可以通过文件通道读写文件。它无法设置为非阻塞模式，总是运行在阻塞模式下。

## 打开FileChannel

可以通过使用一个InputStream、OutputStream或RandomAccessFile来获取一个FileChannel实例。

#### 例如：

```java
RandomAccessFile aFile = new RandomAccessFile("D:/demo/data.txt", "rw");
FileChannel inChannel = aFile.getChannel();
```

## 读FileChannel
调用FileChannel.read()方法，read()方法返回的int值表示了有多少字节被读到了Buffer中。如果返回-1，表示到了文件末尾。

#### 例如：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
int bytesRead = inChannel.read(buf);
```

## 写FileChannel

调用FileChannel.write()方法，因为无法保证write()方法一次能向FileChannel写入多少字节，因此需要重复调用write()方法，直到Buffer中已经没有尚未写入通道的字节。

#### 例如：
```java
String newData = "Some data";
ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());
buf.flip();
while(buf.hasRemaining()) {
    channel.write(buf);
}
```

## 关闭FileChannel

用完FileChannel后必须将其关闭，

#### 例如：
```java
channel.close();
```

## FileChannel的位置

调用position()方法可以获取FileChannel的当前位置，调用position(long pos)方法设置FileChannel的当前位置。例如：

```java
long pos = channel.position();
channel.position(pos + 100);

```

如果将位置设置在文件结束符之后，然后从通道中读数据，读方法将返回-1 ，也就是文件结束标志。 
如果将位置设置在文件结束符之后，然后向通道中写数据，文件将撑大到当前位置并写入数据。这可能导致“文件空洞”，磁盘上物理文件中写入的数据间有空隙。

## 获取文件的大小
```java
long fileSize = channel.size();
```

## 截取文件
调用FileChannel.truncate()方法，截取文件时，文件中指定长度后面的部分将被删除。如：

```java
channel.truncate(1024);
```

## 强制写入磁盘
操作系统一般会将数据缓存在内存中，写入到FileChannel里的数据不一定会即时写到磁盘上。调用force()方法可以将通道里尚未写入磁盘的数据强制写到磁盘上。 
force()方法有一个boolean类型的参数，true表示同时将文件元数据（权限信息等）写到磁盘上，例如：

```java
channel.force(true);
```
