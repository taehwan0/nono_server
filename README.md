# NoNo Deluxe
- Java, OpenJDK@11
- SpringBoot 2.7.0
- Gradle

### Package 구조

- controller : `@Controller` 또는 `@RestController` url 에 매핑되는 컨트롤러 패키지
  - dto : Http Request, Response 시 사용하는 DTO 를 관리하는 패키지
- service : 비지니스 로직, `@Service` 가 위치하는 패키지
- domain : 하위에 entity 와 repository 를 두는 Model
  - entity(ex:notice) : 실제 DB 와 매핑되는 `@Entity` 객체들을 담는 패키지
- config : `@Configuration` 어노테이션을 사용하는 설정과 관련된 클래스들의 패키지

### Pattern

- `@Builder`패턴은 `@Entity` 클래스의 객체를 생성 할 때만 사용한다.
- `Dto` 클래스의 객체를 생성할 시에는 `new` 연산자를 사용해 생성한다.