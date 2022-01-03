import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable{

    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Lock rlock = new ReentrantLock();
    private final Lock wlock = new ReentrantLock();

    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) { this.tag = tag; this.data = data; }
    }

    public TaggedConnection(Socket socket) throws IOException{
        this.socket = socket;
        this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException{
        try{
            wlock.lock();
            os.writeInt(4+data.length);
            os.writeInt(tag);
            os.write(data);
            os.flush();
        } finally { wlock.unlock(); }
    }

    public Frame receive() throws IOException{
        int tag;
        try{
            rlock.lock();
            byte[] data = new byte[is.readInt()-4];
            tag = is.readInt();
            is.readFully(data);
            return new Frame(tag,data);
        } finally { rlock.unlock(); }
    }

    public void close() throws IOException {
        this.is.close();
        this.os.close();
    }
}
