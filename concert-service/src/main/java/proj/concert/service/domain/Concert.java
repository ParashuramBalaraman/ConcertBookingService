package proj.concert.service.domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicMarkableReference;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;

@Entity
public class Concert {
    @Id
    @GeneratedValue
    @Column(name = "concertId", nullable = false, unique = true)
    private Long concertId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "imageName", nullable = false)
    private String imageName;
    @Column(name = "blurb", nullable = false)
    private String blurb;
    @ManyToOne(fetch =FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.REMOVE})//might be one to many need to recheck
    @JoinTable(name = "concert performer", joinColumns = @JoinColumn(name = "concertId"))
    @Column(name = "performers", nullable = false, unique = true)

    private Set<Performer> performers;
    @ElementCollection
    @CollectionTable(name = "concertDates")

    private Set<LocalDateTime> dates;

    public Concert(){}
    public Concert(Long id,String title,String imageName,String blurb,Set<LocalDateTime>dates){
        this.concertId = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
        this.dates = dates;

    }
    public Long getId() {
        return concertId;
    }

    public void setId(Long index) {
        concertId = index;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        title = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String name) {
        imageName = name;
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

    ;

    public ConcertDTO translateToDTO() {
        ConcertDTO concertDTO = new ConcertDTO(concertId, title, imageName, blurb);
        ArrayList<PerformerDTO> performersAL = new ArrayList<PerformerDTO>();
        for (Performer p : performers) {
            performersAL.add(p.translatetoDTO());
        }
        concertDTO.setPerformers(performersAL);

        ArrayList<LocalDateTime> datesAL = new ArrayList<LocalDateTime>();
        for (LocalDateTime d : dates) {
            datesAL.add(d);
        }
        concertDTO.setDates(datesAL);
        return concertDTO;
    }
}

