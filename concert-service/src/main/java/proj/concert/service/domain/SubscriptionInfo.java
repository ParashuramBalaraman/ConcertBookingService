package proj.concert.service.domain;

import proj.concert.common.dto.ConcertInfoSubscriptionDTO;

import javax.ws.rs.container.AsyncResponse;

public class SubscriptionInfo {

    private AsyncResponse sub;
    private ConcertInfoSubscriptionDTO subInfo;

    public SubscriptionInfo(AsyncResponse sub, ConcertInfoSubscriptionDTO subInfo) {
        this.sub = sub;
        this.subInfo = subInfo;
    }

    public AsyncResponse getAsyncResponse() {
        return this.sub;
    }

    public ConcertInfoSubscriptionDTO getSubInfo() {
        return this.subInfo;
    }
}