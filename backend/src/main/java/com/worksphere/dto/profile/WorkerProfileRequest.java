package com.worksphere.dto.profile; import java.util.List; public record WorkerProfileRequest(String location,String bio,String availability,List<String> skills) {}
