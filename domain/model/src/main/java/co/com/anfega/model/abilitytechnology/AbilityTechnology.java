package co.com.anfega.model.abilitytechnology;

public class AbilityTechnology {

    ;
    private Long abilityId;
    private Long technologyId;

    public AbilityTechnology() {
    }

    public AbilityTechnology(Long abilityId, Long technologyId) {
        this.abilityId = abilityId;
        this.technologyId = technologyId;
    }

    public Long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Long abilityId) {
        this.abilityId = abilityId;
    }

    public Long getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(Long technologyId) {
        this.technologyId = technologyId;
    }
}
