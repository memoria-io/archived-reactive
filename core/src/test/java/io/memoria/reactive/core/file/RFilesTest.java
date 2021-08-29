package io.memoria.reactive.core.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RFilesTest {
  private static final Logger log = LoggerFactory.getLogger(RFilesTest.class.getName());
  private static final String tmp = "/tmp";
  private static final String emptyDir = "file/emptyDir";
  private static final String nonEmptyDir = "file/nonEmptyDir";

  @BeforeEach
  void beforeEach() {

    //    RFile.clean(emptyDir).subscribe();
  }

  @Test
  void readDirectory() {
    //    var files1 = RFiles.directory(tmp).get();
    //    var files2 = RFiles.directory(nonEmptyDir).get();
    //    System.out.println(files1);
    //    System.out.println(files2);
  }

  //  @Test
  //  void watch() {
  //    var pub = RFiles.publish(emptyDir, Flux.just(Tuple.of("f1", "hello"), Tuple.of("f2", "hi"), Tuple.of("f3", "bye")));
  //    StepVerifier.create(pub).expectNextCount(3).verifyComplete();
  //    var sub = RFiles.subscribe(emptyDir, 0).take(3).subscribe(System.out::println);
  //  }

  @Test
  @DisplayName("Should append or create a file")
  void appendOrCreate() {
    // When
    var filePath = "target/temp.txt";
    //    var writeFileMono = RFiles.write(filePath, "hello world");
    //    var fileExistsMono = writeFileMono.map(h -> Path.of(filePath).toFile().exists());
    //     Then
    //    StepVerifier.create(writeFileMono).expectNextCount(1).expectComplete().verify();
    //    StepVerifier.create(fileExistsMono).expectNext(true).expectComplete().verify();
  }

  //  @Test
  //  @DisplayName("Should read file as resources file if it's relative path")
  //  void readFile() throws IOException {
  //    var is = RFiles.inputStream("Config.yaml").get();
  //    var expected = is.readAllBytes();
  //    var actual = Files.readAllBytes(Path.of(ClassLoader.getSystemResource("Config.yaml").getPath()));
  //    assertEquals(new String(expected), new String(actual));
  //  }
}
