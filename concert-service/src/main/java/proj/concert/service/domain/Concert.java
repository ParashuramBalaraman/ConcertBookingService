package proj.concert.service.domain;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import proj.concert.common.dto.ConcertDTO;


@Entity
@Table(name="CONCERTS")
public class Concert {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
@Column(name="TITLE")
    private String title;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @Column(name = "BLURB",length = 1000)//allows blurb to be longer than 100 chars
    private String blurb;

    //can use element collection for handling date relation as it isn't an entity
    @ElementCollection(fetch=FetchType.EAGER)//want to get all associated dates when found initially
    @CollectionTable(name = "CONCERT_DATES",joinColumns = @JoinColumn(name="CONCERT_ID"))//date tied to primary key of concert
    @Column(name="DATE")
    private Set<LocalDateTime> dates = new HashSet<>();
    @ManyToMany(cascade =CascadeType.PERSIST)//concerts can have many performers and performers can be attatched to mulitple concerts
    @JoinTable(name = "CONCERT_PERFORMER",joinColumns = @JoinColumn(name= "CONCERT_ID"),inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))//link the two primary key columns
    @Fetch(FetchMode.SUBSELECT)//use subselect for scalability performance
    @Column(name = "PERFORMER_ID")
    private Set<Performer> performers = new HashSet<>();
    public Concert(){}
    public Concert(Long id,String title,String imageName,String blurb,Set<LocalDateTime>dates){
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
        this.dates = dates;

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }


    public Set<Performer> getPerformers() {return performers;}

    public void setPerformers(Set<Performer> performers) {this.performers = performers;}

    ;

    public Set<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(Set<LocalDateTime> dates) {this.dates = dates;}


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
                append(title, rhs.title).
                isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(title).hashCode();
    }


}

