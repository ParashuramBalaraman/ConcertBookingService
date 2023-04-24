package proj.concert.service.domain;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.util.*;
@Entity
@Table(name="PERFORMERS")
public class Performer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
@Column(name="NAME")
    private String name;
    @Column(name = "IMAGE_NAME")
    private String imageName;
@Column(name="GENRE")
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(name = "BLURB",length = 1000)//length allows the blurb to be over 1000chars
    private String blurb;



    public Performer(){};
    public Performer(Long id,String name,String imageName,Genre genre,String blurb){
        this.Id = id;
        this.name=name;
        this.imageName=imageName;
        this.genre = genre;
        this.blurb=blurb;

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
