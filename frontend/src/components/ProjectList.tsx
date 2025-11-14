// Refactored ProjectList component with stable keys for responsiblePeople
// Replace your current file with this one and adjust types as needed
import React from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

export interface ResponsiblePerson {
  id: string;
  name: string;
}

export interface Project {
  id: string;
  name: string;
  description: string;
  responsiblePeople: ResponsiblePerson[];
}

interface ProjectListProps {
  readonly projects: Readonly<Project[]>;
  readonly onOpenProject: (projectId: string) => void;
}

export default function ProjectList({ projects, onOpenProject }: ProjectListProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 p-4">
      {projects.map((project) => (
        <Card
          key={project.id}
          className="cursor-pointer hover:shadow-lg transition-shadow"
          onClick={() => onOpenProject(project.id)}
        >
          <CardContent className="p-6">
            <h3 className="text-xl font-semibold mb-2">{project.name}</h3>
            <p className="text-gray-600 text-sm mb-4 line-clamp-3">
              {project.description}
            </p>

            {project.responsiblePeople.length > 0 && (
              <div className="flex flex-wrap gap-1 mt-3">
                {project.responsiblePeople.slice(0, 3).map((person) => (
                  <Badge key={person.id} variant="secondary" className="text-xs">
                    {person.name}
                  </Badge>
                ))}

                {project.responsiblePeople.length > 3 && (
                  <Badge variant="secondary" className="text-xs">
                    +{project.responsiblePeople.length - 3}
                  </Badge>
                )}
              </div>
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
}