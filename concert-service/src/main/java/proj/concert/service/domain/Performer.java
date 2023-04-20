package proj.concert.service.domain;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicLong;
@Entity
@Table(name="PERFORMERS")
public class Performer {
    @Id

    @Column(name="ID")
    private Long Id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @Column(name = "GENRE")
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(name = "BLURB")
    private String blurb;



    public Performer(){};
    public Performer(Long id,String name,String imageName,Genre genre,String blurb){
        this.Id = id;
        this.name=name;
        this.imageName=imageName;
        this.genre = genre;
        this.blurb=blurb;

    }

    public PerformerDTO translatetoDTO(){
        PerformerDTO performerDTO = new PerformerDTO(Id,name,imageName,genre,blurb);
        return performerDTO;
    }

    public Long getId(){return Id;}
    public void setId(Long id){this.Id=id;}
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    public String getImageName(){return this.imageName;}
    public void setImageName(String imageName){this.imageName=imageName;}
    public Genre getGenre(){return this.genre;}
    public void setGenre(Genre genre){this.genre = genre;}
    public String getBlurb(){return this.blurb;}
    public void setBlurb(String blurb){this.blurb = blurb;}

}
