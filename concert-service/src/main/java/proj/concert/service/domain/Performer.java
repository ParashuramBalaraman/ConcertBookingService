package proj.concert.service.domain;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicLong;
@Entity
@Table(name="Performers")
public class Performer {
    @Id
    @GeneratedValue
    @Column(name="performerId",nullable = false)
    private Long performerId;
    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "imageName",nullable = false)
    private String imageName;
    @Column(name = "genre",nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(name = "blurb")
    private String blurb;

    public Performer(){};

    public PerformerDTO translatetoDTO(){
        PerformerDTO performerDTO = new PerformerDTO(performerId,name,imageName,genre,blurb);
        return performerDTO;
    }

    public Long getId(){return performerId;}
    public void setId(Long id){this.performerId=id;}
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    public String getImageName(){return this.imageName;}
    public void setImageName(String imageName){this.imageName=imageName;}
    public Genre getGenre(){return this.genre;}
    public void setGenre(Genre genre){this.genre = genre;}
    public String getBlurb(){return this.blurb;}
    public void setBlurb(String blurb){this.blurb = blurb;}

}
