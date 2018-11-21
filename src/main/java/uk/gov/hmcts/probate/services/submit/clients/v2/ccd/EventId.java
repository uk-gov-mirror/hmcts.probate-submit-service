package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

public enum EventId {

    CREATE_DRAFT("createDraft"), UPDATE_DRAFT("updateDraft");

    private String name;

    EventId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
