import java.awt.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private TaggedConnection con;
    private ReentrantLock lock = new ReentrantLock();
    private Map<Integer,Entry> buffer = new HashMap<>();
    private IOException exception = null;

    private class Entry {
        int waiters = 0;
        ArrayDeque<byte[]> queue = new ArrayDeque<>();
        Condition cond = lock.newCondition();
    }

    private Entry get(int tag){
        Entry entry = buffer.get(tag);
        if(entry == null) {
            entry = new Entry();
            buffer.put(tag,entry);
        }
        return entry;
    }

    public Demultiplexer(TaggedConnection connection) throws IOException{
        this.con = connection;
    }

    public void start() throws IOException{
        new Thread(() -> {
            try {
                while(true){
                    TaggedConnection.Frame frame = con.receive();
                    lock.lock();
                    try{
                        Entry entry = buffer.get(frame.tag);
                        if(entry == null){
                            entry = new Entry();
                            buffer.put(frame.tag,entry);
                        }
                        entry.queue.add(frame.data);
                        entry.cond.signal();
                    }
                    finally { lock.unlock(); }
                }
            }
            catch(IOException e){
                exception = e;
            }
        }).start();
    }

    public void send(TaggedConnection.Frame frame) throws IOException {
        con.send(frame);
    }
    public void send(int tag, byte[] data) throws IOException {
        con.send(tag,data);
    }
    public byte[] receive(int tag) throws IOException, InterruptedException{
        lock.lock();
        Entry entry;
        try{
            entry = buffer.get(tag);
            if(entry == null){
                entry = new Entry();
                buffer.put(tag, entry);
            }
            entry.waiters++;
            while(true){
                if(!entry.queue.isEmpty()){
                    entry.waiters--;
                    byte[] reply = entry.queue.poll();
                    if(entry.waiters == 0 && entry.queue.isEmpty())
                        buffer.remove(tag);
                    return reply;
                }
                if(exception != null) { throw exception; }
                entry.cond.await();
            }
        }
        finally{ lock.unlock(); }
    }
    public void close() throws IOException {
        con.close();
    }
}