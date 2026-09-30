package com.smartspeak.english_app.dataAssets.datacontroller;

import com.smartspeak.english_app.dataAssets.grammar.GrammarRepository;
import com.smartspeak.english_app.dataAssets.grammar.GrammarTheory;
import com.smartspeak.english_app.dataAssets.reading.ReadingRepository;
import com.smartspeak.english_app.dataAssets.reading.ReadingTheory;
import com.smartspeak.english_app.dataAssets.listening.ListeningRepository;
import com.smartspeak.english_app.dataAssets.listening.ListeningTheory;
import com.smartspeak.english_app.dataAssets.speaking.SpeakingRepository;
import com.smartspeak.english_app.dataAssets.speaking.SpeakingTheory;
import com.smartspeak.english_app.dataAssets.writing.WritingRepository;
import com.smartspeak.english_app.dataAssets.writing.WritingTheory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class DataController {

    private final ListeningRepository listeningRepository;
    private final ReadingRepository readingRepository;
    private final WritingRepository writingRepository;
    private final SpeakingRepository speakingRepository;
    private final GrammarRepository grammarRepository;

    @PostMapping("/create-listening")
    public ListeningTheory addListening(@RequestBody ListeningTheory newTask) {
        return listeningRepository.save(newTask);
    }
    @GetMapping("/listening")
    public List<ListeningTheory> getAllListening() {
        return listeningRepository.findAll();
    }
    @PostMapping("/create-reading")
    public ReadingTheory addReading(@RequestBody ReadingTheory newTask) {
        return readingRepository.save(newTask);
    }
    @GetMapping("/reading")
    public List<ReadingTheory> getAllReading() {
        return readingRepository.findAll();
    }
    @PostMapping("/create-writing")
    public WritingTheory addWriting(@RequestBody WritingTheory newTask) {
        return writingRepository.save(newTask);
    }
    @GetMapping("/writing")
    public List<WritingTheory> getAllWriting() {
        return writingRepository.findAll();
    }
    @PostMapping("/create-speaking")
    public SpeakingTheory addSpeaking(@RequestBody SpeakingTheory newTask) {
        return speakingRepository.save(newTask);
    }
    @GetMapping("/speaking")
    public List<SpeakingTheory> getAllSpeaking() {
        return speakingRepository.findAll();
    }
    @PostMapping("/create-grammar")
    public GrammarTheory addGrammar(@RequestBody GrammarTheory newTask) {
        return grammarRepository.save(newTask);
    }
    @GetMapping("/grammar")
    public List<GrammarTheory> getAllGrammar() {
        return grammarRepository.findAll();
    }
}

