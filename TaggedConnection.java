import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
            os.writeInt(data.length);
            os.writeInt(tag);
            os.write(data);
            os.flush();
        } finally {
            wlock.unlock():
        }
    }

    public Frame receive() throws IOException{
        int tag;
        byte[] data;
        try{
            rlock.lock();
            tag = this.is.readInt();
            int n = this.is.readInt();
            data = new byte[n];
            this.is.readFully(data);
        } finally {
            rlock.unlock():
        }
        return new Frame(tag,data);
    }

    public void close() throws IOException {
        this.is.close();
        this.os.close();
    }
}