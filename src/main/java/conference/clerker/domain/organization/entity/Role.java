package conference.clerker.domain.organization.entity;

public enum Role {
    OWNER, MEMBER;

    @Override
    public String toString() {
        return "ROLE_" + super.toString();
    }
}
