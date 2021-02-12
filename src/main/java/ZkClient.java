import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZkClient {

    private static String CONNECT_STRING = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private ZooKeeper zooKeeper;

    @Before
    public void before() throws IOException {
        zooKeeper = new ZooKeeper(
                CONNECT_STRING,
                6000,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("默认的回调函数");
                    }
                }
        );
    }

    @After
    public void after() throws InterruptedException {

        //3. 关闭资源
        zooKeeper.close();
    }

    @Test
    public void ls() throws IOException, KeeperException, InterruptedException {

        //2. 操作
//        List<String> children = zooKeeper.getChildren("/", true);
        List<String> children = zooKeeper.getChildren("/",
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("自定义回调函数");
                    }
                });

        for (String child : children) {
            System.out.println(child);
        }

        Thread.sleep(Long.MAX_VALUE);

    }


    @Test
    public void get() throws KeeperException, InterruptedException, IOException {
        Stat stat = new Stat();
        //获取节点数据
        byte[] data = zooKeeper.getData("/testsss", true, stat);

        System.out.write(data);
        System.out.println();

        System.out.println(stat.getCzxid());
    }

    @Test
    public void create() throws KeeperException, InterruptedException {
        String line = zooKeeper.create(
                "/testAPI",
                "testAPI".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
    }

    @Test
    public void stat() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists("/testAPI", false);

        if (stat == null) {
            System.out.println("节点不存在");
        } else {
            System.out.println(stat.getDataLength());
        }
    }

    @Test
    public void set() throws KeeperException, InterruptedException {

        Stat stat = zooKeeper.exists("testAPI", false);
        if (stat != null)
            zooKeeper.setData(
                    "/testAPI",
                    "newData".getBytes(),
                    stat.getVersion()
            );
    }

    @Test
    public void deleteall() throws KeeperException, InterruptedException {
        delete("/testzxid");
    }

    public void delete(String znode) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(znode, false);

        if (stat != null) {

            List<String> children = zooKeeper.getChildren(znode, false);

            if (children.size() == 0) {
                zooKeeper.delete(znode, stat.getVersion());
            } else {
                for (String child : children) {
                    delete(znode + "/" + child);
                }
            }
        }
    }
}
