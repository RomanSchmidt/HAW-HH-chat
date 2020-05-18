package chat;

import java.util.Objects;

public class Uid implements Comparable<Uid> {
    private final String ip;
    private final int port;

    public Uid(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String toString() {
        return this.ip + ":" + this.port;
    }

    @Override
    public int compareTo(Uid o) {
        return this.port == o.port && this.ip.equals(o.ip) ? 0 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uid uid = (Uid) o;
        return port == uid.port && Objects.equals(ip, uid.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
