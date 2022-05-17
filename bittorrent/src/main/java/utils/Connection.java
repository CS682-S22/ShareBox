package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author anchit bhatia
 * @author alberto delgado
 * <p>
 * Connection object helper class to establish connection to remote node,
 * send and receive messages
 */
public class Connection {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Method to retrieve remote port number
     *
     * @return remote por number
     */
    public int getPort() {
        return socket.getPort();
    }

    /**
     * Method to retrieve the remote address
     *
     * @return String of the remote address
     */
    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    /***
     * Method to receive bytes
     * @return bytes read
     */
    public byte[] receive() {
        byte[] buffer = null;
        try {
            int length = this.inputStream.readInt();
            if (length > 0) {
                buffer = new byte[length];
                this.inputStream.readFully(buffer, 0, buffer.length);
            }
        } catch (EOFException ignored) {
        } //No more content available to read
        catch (SocketException exception) {
            return null;
        } catch (IOException exception) {
            System.out.println("IO exception while reading message");
        }
        return buffer;
    }

    /***
     * Method to send bytes
     * @param message : message to be sent
     */
    public void send(byte[] message) throws ConnectionException {
        try {
            if (!this.socket.isClosed()) {
                this.outputStream.writeInt(message.length);
                this.outputStream.write(message);
            }
        } catch (SocketException e) {
            throw new ConnectionException("Unable to send. Broken pipe.");
        } catch (IOException e) {
            System.out.println("IO exception while sending message");
        }
    }

    /***
     * Method to check if connection is closed
     * @return true if connection is closed else false
     */
    public boolean isClosed() {
        return this.socket.isClosed();
    }

    /***
     * Method to close connection
     */
    public void close() throws IOException {
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }
}
