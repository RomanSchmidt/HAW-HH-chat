enum MessageType {
    chatMessage = 1,
    routingResponse = 2,
    routingRequest = 3,
    connect = 4,
    disconnect = 5
}

// Parameter in all requests
type Header = {
    type: MessageType;
    senderName?: String; // länge = 3-20 nur type1 => warum ist es dann ein allgemeiner header?
    UIDSender: String; // ip:port
    UIDReceiver: String; // ip:port
}

// just for routing requests
type RoutingTableElement = {
    destinationIP: String;
    destinationPort: number;
    costsToDestination: number; // Anzahl der hops / 0 bei eigener ip
    changedRecently: boolean; //  flag die angibt, ob sich an der  Route kürzlich etwas geändert hat
    nextGatewayIP: String,
    nextGatewayPort: number
}

type RoutingRequest = {
    header: Header & {
        type: MessageType.routingRequest;
    };
    routingTable: RoutingTableElement[];
}

type RoutingResponse = {
    header: Header & {
        type: MessageType.routingResponse;
    };
    routingTable: RoutingTableElement[];
}

type ChatMassage = {
    header: Header & {
        type: MessageType.chatMessage;
        senderName: String
    };
    message: String; // length = ?
}

type ConnectMassage = {
    header: Header & {
        type: MessageType.connect;
    };
}

type DisconnectMessage = {
    header: Header & {
        type: MessageType.disconnect;
    };
}

