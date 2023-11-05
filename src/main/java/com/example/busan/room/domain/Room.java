package com.example.busan.room.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.util.Assert;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String image;
    private int maxPeopleCount;

    protected Room() {
    }

    public Room(final Long id, final String name, final String image, final Integer maxPeopleCount) {
        validate(name, image, maxPeopleCount);
        this.id = id;
        this.name = name;
        this.image = image;
        this.maxPeopleCount = maxPeopleCount;
    }

    private void validate(final String name, final String image, final Integer maxPeopleCount) {
        Assert.notNull(name, "이름이 필요합니다.");
        Assert.notNull(image, "이미지가 필요합니다.");
        Assert.notNull(maxPeopleCount, "최대 수용 인원 수가 필요합니다.");
    }

    public Room(final String name, final String image, final Integer maxPeopleCount) {
        this(null, name, image, maxPeopleCount);
    }

    public void update(final String name, final String image, final Integer maxPeopleCount) {
        validate(name, image, maxPeopleCount);
        this.name = name;
        this.image = image;
        this.maxPeopleCount = maxPeopleCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getMaxPeopleCount() {
        return maxPeopleCount;
    }
}
