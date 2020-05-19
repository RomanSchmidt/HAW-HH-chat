enum MessageType {
    chatMessage = 1,
    routingResponse = 2,
    routingRequest = 3,
    connect = 4,
    disconnect = 5
}

type Uid = {
    ip: String;
    port: number;
}

// Parameter in all requests
type Header = {
    type: MessageType;
    uidSender: Uid;
    uidReceiver: Uid;
}

// just for routing requests
type RoutingTableElement = {
    destinationUid: Uid;
    userName: String; // länge = 3-20
    costsToDestination: number; // Anzahl der hops / 0 bei eigener ip
}

type RoutingResponse = {
    header: Header & {
        type: MessageType.routingResponse;
    };
    content: {
        routingTable: RoutingTableElement[];
    }
}

type ChatMassage = {
    header: Header & {
        type: MessageType.chatMessage
    };
    content: {
        message: String; // length = ?
    }
}

type Connect = {
    header: Header & {
        type: MessageType.connect;
    };
    userName: String; // länge = 3-20
}

type DisconnectMessage = {
    header: Header & {
        type: MessageType.disconnect;
    };
}

