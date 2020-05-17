package chat.client;

import chat.Uid;

public abstract class AClient implements Actor {
    protected final String _name;
    protected final Uid _uid;

    AClient(Uid uid, String name) {
        this._uid = uid;
        this._name = name;
    }

    public Uid getUid() {
        return this._uid;
    }

    public String getName() {
        return this._name;
    }

    /**
     * @todo send disco message
     */
    public void disconnect() {
        System.err.println("disconnect not implemented in foreign client");
    }
}
