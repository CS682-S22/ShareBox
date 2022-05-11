package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Connection {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    /***
     * Method to receive bytes
     * @return bytes read
     */
    public byte[] receive(){
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
}
