package proj.concert.service.domain;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicLong;
@Entity
@Table(name="PERFORMERS")
public class Performer {
    @Id
    @GeneratedValue
    @Column(name="ID",nullable = false)
    private Long performerId;
    @Column(name = "NAME",nullable = false)
    private String name;
    @Column(name = "IMAGE_NAME",nullable = false)
    private String imageName;
    @Column(name = "GENRE",nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(name = "BLURB")
    private String blurb;

    public Performer(){};
    public Performer(Long id,String name,String imageName,Genre genre,String blurb){
        this.performerId = id;
        this.name=name;
        this.imageName=imageName;
        this.genre = genre;
        this.blurb=blurb;

    }

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
