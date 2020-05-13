enum MessageType {
    chatMessage = 1,
    routingResponse = 2,
    routingRequest = 3,
    connect = 4,
    disconnect = 5
}

type UID = {
    IP: String;
    Port: number;
}

// Parameter in all requests
type Header = {
    type: MessageType;
    UIDSender: UID;
    UIDReceiver: UID;
}

// just for routing requests
type RoutingTableElement = {
    destinationUID: UID;
    senderName: String; // länge = 3-20
    costsToDestination: number; // Anzahl der hops / 0 bei eigener ip
}

type RoutingRequest = {
    header: Header & {
        type: MessageType.routingRequest;
    };
    content: {
        routingTable: RoutingTableElement[];
    }
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
    senderName: String; // länge = 3-20
}

type DisconnectMessage = {
    header: Header & {
        type: MessageType.disconnect;
    };
}

